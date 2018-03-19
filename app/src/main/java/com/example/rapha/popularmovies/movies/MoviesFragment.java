package com.example.rapha.popularmovies.movies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MoviesDatabaseContract;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.services.TmdbFetchIntentService;

public class MoviesFragment extends Fragment implements
        MoviesAdapter.OnGridItemClickedHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MoviesDatabaseContract.MovieEntry._ID,
            MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesDatabaseContract.MovieEntry.COLUMN_TITLE,
            MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE
    };
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    public static final int INDEX_IS_FAVORITE = 3;

    private final String TAG = getClass().getSimpleName();
    private final int MOVIES_LOADER_ID = 4563;

    private final String STATE_SORT_ORDER_KEY = "sort_order";
    private final String STATE_PAGE_TO_LOAD_KEY = "page_to_load";
    private final String STATE_RECYCLER_VIEW_STATE_KEY = "recycler_view_state";
    private final String STATE_NO_CONNECTION_VISIBILITY_KEY = "no_connection_visbility";
    private final String STATE_SPINNER_POSITION_KEY = "spinner_position";

    private MoviesAdapter moviesAdapter;
    private TextView noConnectionTv;
    private RecyclerView posterRv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int spinnerPosition;
    private int pageToLoad = 1;
    private String sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY;

    public MoviesFragment() {
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        noConnectionTv = view.findViewById(R.id.no_connection_tv);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        posterRv = view.findViewById(R.id.posters_rv);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refreshing data");
                resetCurrentPage();
                loadData();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        moviesAdapter = new MoviesAdapter(this);
        final int orientation = getContext().getResources().getConfiguration().orientation;
        int columnSpan = orientation == Configuration.ORIENTATION_PORTRAIT
                ? getResources().getInteger(R.integer.gridview_portrait_columns)
                : getResources().getInteger(R.integer.gridview_landscape_columns);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columnSpan);
        posterRv.setLayoutManager(layoutManager);
        posterRv.setAdapter(moviesAdapter);
        posterRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    loadData();
                }
            }
        });

        if (savedInstanceState == null) {
            loadData();
        } else {
            restoreViewState(savedInstanceState);
        }

        getActivity().getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);

        setHasOptionsMenu(true);

        return view;
    }

    private void restoreViewState(Bundle savedInstanceState) {
        spinnerPosition = savedInstanceState.getInt(STATE_SPINNER_POSITION_KEY);
        pageToLoad = savedInstanceState.getInt(STATE_PAGE_TO_LOAD_KEY);
        sortOrder = savedInstanceState.getString(STATE_SORT_ORDER_KEY);
        Parcelable rvState = savedInstanceState.getParcelable(STATE_RECYCLER_VIEW_STATE_KEY);
        posterRv.getLayoutManager().onRestoreInstanceState(rvState);
        noConnectionTv.setVisibility(savedInstanceState.getInt(STATE_NO_CONNECTION_VISIBILITY_KEY));
    }

    public void setSortOrder(String sortOrder) {
        Log.d(TAG, "Setting sort order to: " + sortOrder);
        this.sortOrder = sortOrder;
        resetCurrentPage();
        loadData();
    }

    private void resetCurrentPage() {
        pageToLoad = 1;
    }

    private void incrementCurrentPage() {
        pageToLoad++;
    }

    private void loadData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            fetchRemoteData();
        } else {
            showNoConnectionMessage();
        }
        loadLocalData();
    }

    private void loadLocalData() {
        showProgress();
        getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    private void fetchRemoteData() {
        Intent intentService = new Intent(getContext(), TmdbFetchIntentService.class);
        if (sortOrder.equals(MoviesDatabaseContract.MovieEntry.COLUMN_RATING)) {
            intentService.setAction("fetch_top_rated_movies");
        } else intentService.setAction("fetch_popular_movies");
        intentService.putExtra("page", pageToLoad);
        getActivity().startService(intentService);
        Log.d(TAG, "Fetching movies page " + pageToLoad);
    }

    private void showProgress() {
        swipeRefreshLayout.setRefreshing(true);
        noConnectionTv.setVisibility(View.GONE);
    }

    private void hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showNoConnectionMessage() {
        if (moviesAdapter.getItemCount() > 0) {
            Toast.makeText(getContext(), getString(R.string.main_no_connection), Toast.LENGTH_LONG).show();
        } else {
            noConnectionTv.setVisibility(View.VISIBLE);
        }
        hideProgress();
    }

    @Override
    public void onItemClicked(int movieId) {
        Log.d(TAG, "Selected movie id: " + movieId);
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.setData(MoviesDatabaseContract.MovieEntry.buildMovieEntryUri(movieId));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE_TO_LOAD_KEY, pageToLoad);
        outState.putString(STATE_SORT_ORDER_KEY, sortOrder);
        Parcelable rvState = posterRv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_RECYCLER_VIEW_STATE_KEY, rvState);
        outState.putInt(STATE_SPINNER_POSITION_KEY, spinnerPosition);
        outState.putInt(STATE_NO_CONNECTION_VISIBILITY_KEY, noConnectionTv.getVisibility());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_overview_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.sort_order_spinner);
        Spinner spinner = (Spinner) menuItem.getActionView();
        //spinner.setSelection(spinnerPosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != spinnerPosition) {
                    String item = (String) parent.getItemAtPosition(position);
                    if (item.equals(getString(R.string.menu_spinner_popular))) {
                        sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY;
                    } else if (item.equals(getString(R.string.menu_spinner_top_rated))) {
                        sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_RATING;
                    } else {

                    }
                    Log.d(TAG, "Sort criteria changed to: " + item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case MOVIES_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.MovieEntry.CONTENT_URI, MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortOrder + " DESC");
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIES_LOADER_ID:
                moviesAdapter.swapCursor(data);
                hideProgress();
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MOVIES_LOADER_ID:
                moviesAdapter.swapCursor(null);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }
}
