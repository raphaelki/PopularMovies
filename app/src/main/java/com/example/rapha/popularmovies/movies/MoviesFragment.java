package com.example.rapha.popularmovies.movies;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MovieRepository;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.utils.Constants;

public class MoviesFragment extends Fragment implements
        MoviesAdapter.OnGridItemClickedHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();
    private final int ASYNC_MOVIES_LOADER_ID = 2325;
    private final int INITIAL_PAGES_TO_FETCH = 3;

    private final String POPULAR = "popular";
    private final String TOP_RATED = "top_rated";
    private final String FAVORITE = "favorite";
    private final String STATE_PAGE_TO_LOAD_KEY = "page_to_load";
    private final String STATE_RECYCLER_VIEW_STATE_KEY = "recycler_view_state";
    private final String STATE_NO_CONNECTION_VISIBILITY_KEY = "no_connection_visbility";
    private String sortOrder = POPULAR;
    private MoviesAdapter moviesAdapter;
    private TextView noConnectionTv;
    private RecyclerView posterRv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;

    private MovieRepository movieRepository;

    private int pageToLoad = 1;

    public MoviesFragment() {
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        restartLoader();
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        movieRepository = MovieRepository.getInstance(getContext().getContentResolver());

        noConnectionTv = view.findViewById(R.id.no_connection_tv);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        posterRv = view.findViewById(R.id.posters_rv);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refreshing data");
                fetchRemoteData();
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

                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (bottomNavigationView.getSelectedItemId() != item.getItemId()) {
                    switch (item.getItemId()) {
                        case R.id.action_popular:
                            Log.d(TAG, "Popular movies selected.");
                            sortOrder = POPULAR;
                            swipeRefreshLayout.setEnabled(true);
                            break;
                        case R.id.action_top_rated:
                            Log.d(TAG, "Top rated movies selected.");
                            sortOrder = TOP_RATED;
                            swipeRefreshLayout.setEnabled(true);
                            break;
                        case R.id.action_favorites:
                            Log.d(TAG, "Favorite movies selected.");
                            sortOrder = FAVORITE;
                            swipeRefreshLayout.setEnabled(false);
                            break;
                    }
                    restartLoader();
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            restoreViewState(savedInstanceState);
        }

        getActivity().getSupportLoaderManager().initLoader(ASYNC_MOVIES_LOADER_ID, null, this);

        IntentFilter intentServiceFilter = new IntentFilter(Constants.FETCHING_DATA_FINISHED_ACTION);
        FetchingStateReceiver fetchingStateReceiver = new FetchingStateReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(fetchingStateReceiver, intentServiceFilter);

        return view;
    }

    private void restartLoader() {
        getActivity().getSupportLoaderManager().restartLoader(ASYNC_MOVIES_LOADER_ID, null, this);
    }

    private void restoreViewState(Bundle savedInstanceState) {
        Log.d(TAG, "Restoring view state");
        pageToLoad = savedInstanceState.getInt(STATE_PAGE_TO_LOAD_KEY);
        Parcelable rvState = savedInstanceState.getParcelable(STATE_RECYCLER_VIEW_STATE_KEY);
        posterRv.getLayoutManager().onRestoreInstanceState(rvState);
        noConnectionTv.setVisibility(savedInstanceState.getInt(STATE_NO_CONNECTION_VISIBILITY_KEY));
    }

    private void resetCurrentPage() {
        pageToLoad = 1;
    }

    private void incrementCurrentPage() {
        pageToLoad++;
    }

//    private void loadLocalData() {
//        getActivity().getSupportLoaderManager().restartLoader(ASYNC_MOVIES_LOADER_ID, null, this);
//    }

    private void fetchRemoteData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            movieRepository.initialRemoteFetch(getContext());
        } else {
            showNoConnectionMessage();
        }
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
        intent.putExtra("movie_id", movieId);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE_TO_LOAD_KEY, pageToLoad);
        Parcelable rvState = posterRv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_RECYCLER_VIEW_STATE_KEY, rvState);
        outState.putInt(STATE_NO_CONNECTION_VISIBILITY_KEY, noConnectionTv.getVisibility());
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ASYNC_MOVIES_LOADER_ID:
                return new AsyncTaskLoader<Cursor>(getContext()) {
                    ForceLoadContentObserver contentObserver = new ForceLoadContentObserver();
                    Cursor cursor = null;

                    @Override
                    public void deliverResult(@Nullable Cursor data) {
                        Log.d(TAG, "deliverResult");
                        cursor = data;
                        super.deliverResult(data);
                    }

                    @Override
                    public Cursor loadInBackground() {
                        Log.d(TAG, "Running loader");
                        Cursor cursor = null;
                        switch (sortOrder) {
                            case POPULAR:
                                cursor = movieRepository.getPopularMovies();
                                break;
                            case TOP_RATED:
                                cursor = movieRepository.getTopRatedMovies();
                                break;
                            case FAVORITE:
                                cursor = movieRepository.getFavoriteMovies();
                        }
                        if (cursor != null) cursor.registerContentObserver(contentObserver);
                        return cursor;
                    }

                    @Override
                    protected void onStartLoading() {
                        Log.d(TAG, "onStartLoading");
                        if (cursor != null) {
                            deliverResult(cursor);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    protected void onReset() {
                        Log.d(TAG, "onReset");
                        if (cursor != null) cursor.close();
                        super.onReset();
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished: " + loader.getId());
        switch (loader.getId()) {
            case ASYNC_MOVIES_LOADER_ID:
                if (data.getCount() == 0 && sortOrder.equals(FAVORITE)) {
                    fetchRemoteData();
                }
                moviesAdapter.swapCursor(data);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "Resetting loader: " + loader.getId());
        switch (loader.getId()) {
            case ASYNC_MOVIES_LOADER_ID:
                moviesAdapter.swapCursor(null);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    private class FetchingStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            hideProgress();
        }
    }
}
