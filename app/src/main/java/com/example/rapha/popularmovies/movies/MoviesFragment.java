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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MoviesDatabaseContract;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.services.TmdbFetchIntentService;

public class MoviesFragment extends Fragment implements
        MoviesAdapter.OnGridItemClickedHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    public final static String POPULAR_PATH = "popular";
    public final static String TOP_RATED_PATH = "top_rated";
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MoviesDatabaseContract.MovieEntry._ID,
            MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesDatabaseContract.MovieEntry.COLUMN_TITLE
    };
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    private final String TAG = getClass().getSimpleName();
    private final int MOVIES_LOADER_ID = 0;
    private final String SORT_ORDER_KEY = "sort_order";
    private final String PAGE_TO_LOAD_KEY = "page_to_load";
    private final String RECYCLER_VIEW_STATE_KEY = "recycler_view_state";
    private final String MOVIES_KEY = "movies";
    private final String NO_CONNECTION_VISIBILITY_KEY = "no_connection_visbility";
    private MoviesAdapter moviesAdapter;
    private TextView noConnectionTv;
    private RecyclerView posterRv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int pageToLoad = 1;
    private String sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY;

    public MoviesFragment() {
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

        return view;
    }

    private void restoreViewState(Bundle savedInstanceState) {
        pageToLoad = savedInstanceState.getInt(PAGE_TO_LOAD_KEY);
        sortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
        Parcelable rvState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE_KEY);
        posterRv.getLayoutManager().onRestoreInstanceState(rvState);
        noConnectionTv.setVisibility(savedInstanceState.getInt(NO_CONNECTION_VISIBILITY_KEY));
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
        outState.putInt(PAGE_TO_LOAD_KEY, pageToLoad);
        outState.putString(SORT_ORDER_KEY, sortOrder);
        Parcelable rvState = posterRv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE_KEY, rvState);
        outState.putInt(NO_CONNECTION_VISIBILITY_KEY, noConnectionTv.getVisibility());
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
