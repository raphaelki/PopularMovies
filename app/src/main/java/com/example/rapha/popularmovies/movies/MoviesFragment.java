package com.example.rapha.popularmovies.movies;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.utils.Constants;

public class MoviesFragment extends Fragment implements
        MoviesAdapter.OnGridItemClickedHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();
    private final int POPULAR_MOVIES_LOADER_ID = 2325;
    private final int TOP_RATED_MOVIES_LOADER_ID = 2326;
    private final int FAVORITE_MOVIES_LOADER_ID = 2327;
    private final int POPULAR = 0;
    private final int TOP_RATED = 1;
    private final int FAVORITE = 2;
    private final String STATE_PAGE_TO_LOAD_KEY = "page_to_load";
    private final String STATE_RECYCLER_VIEW_STATE_KEY = "recycler_view_state";
    private final String STATE_NO_CONNECTION_VISIBILITY_KEY = "no_connection_visbility";
    private String[] projection = {
            MoviesDatabaseContract.MovieEntry._ID,
            MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE,
            MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesDatabaseContract.MovieEntry.COLUMN_TITLE
    };
    private Cursor popularMoviesCursor;
    private Cursor topRatedMoviesCursor;
    private Cursor favoriteMovieCursor;
    private int currentTab = POPULAR;
    private MoviesAdapter moviesAdapter;
    private TextView noConnectionTv;
    private RecyclerView posterRv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;

    private MovieRepository movieRepository;

    private int pageToLoad = 1;

    public MoviesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        movieRepository = MovieRepository.getInstance(getContext());

        noConnectionTv = view.findViewById(R.id.no_connection_tv);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        posterRv = view.findViewById(R.id.posters_rv);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refreshing data");
                movieRepository.initialRemoteFetch();
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
                    switch (currentTab) {
                        case TOP_RATED:
                            movieRepository.fetchAdditionalTopRatedMovies();
                            break;
                        case POPULAR:
                            movieRepository.fetchAdditionalPopularMovies();
                            break;
                    }
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
                            moviesAdapter.swapCursor(popularMoviesCursor);
                            swipeRefreshLayout.setEnabled(true);
                            currentTab = POPULAR;
                            break;
                        case R.id.action_top_rated:
                            Log.d(TAG, "Top rated movies selected.");
                            moviesAdapter.swapCursor(topRatedMoviesCursor);
                            swipeRefreshLayout.setEnabled(true);
                            currentTab = TOP_RATED;
                            break;
                        case R.id.action_favorites:
                            Log.d(TAG, "Favorite movies selected.");
                            moviesAdapter.swapCursor(favoriteMovieCursor);
                            swipeRefreshLayout.setEnabled(false);
                            currentTab = FAVORITE;
                            break;
                    }
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            restoreViewState(savedInstanceState);
        }

        getActivity().getSupportLoaderManager().initLoader(POPULAR_MOVIES_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(TOP_RATED_MOVIES_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, this);

        IntentFilter intentServiceFilter = new IntentFilter(Constants.INTENT_SERVICE_BROADCAST_ACTION);
        FetchingStateReceiver fetchingStateReceiver = new FetchingStateReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(fetchingStateReceiver, intentServiceFilter);

        return view;
    }

    private void restoreViewState(Bundle savedInstanceState) {
        Log.d(TAG, "Restoring view state");
        pageToLoad = savedInstanceState.getInt(STATE_PAGE_TO_LOAD_KEY);
        Parcelable rvState = savedInstanceState.getParcelable(STATE_RECYCLER_VIEW_STATE_KEY);
        posterRv.getLayoutManager().onRestoreInstanceState(rvState);
        noConnectionTv.setVisibility(savedInstanceState.getInt(STATE_NO_CONNECTION_VISIBILITY_KEY));
    }

    private void showProgress() {
        Log.d(TAG, "showProgress");
        swipeRefreshLayout.setRefreshing(true);
    }

    private void hideProgress() {
        Log.d(TAG, "hideProgress");
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showNoConnectionMessage() {
        Log.d(TAG, "showNoConnectionMessage");
        if (moviesAdapter.getItemCount() > 0) {
            Toast.makeText(getContext(), getString(R.string.main_no_connection), Toast.LENGTH_LONG).show();
        } else {
            noConnectionTv.setVisibility(View.VISIBLE);
        }
    }

    private void hideNoConnectionMessage() {
        noConnectionTv.setVisibility(View.GONE);
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
        String[] trueSelectionArgs = {"1"};
        switch (id) {
            case POPULAR_MOVIES_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                        projection,
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR + " = ?",
                        trueSelectionArgs,
                        MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY + " DESC");
            case TOP_RATED_MOVIES_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                        projection,
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED + " = ?",
                        trueSelectionArgs,
                        MoviesDatabaseContract.MovieEntry.COLUMN_RATING + " DESC");
            case FAVORITE_MOVIES_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                        projection,
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?",
                        trueSelectionArgs,
                        MoviesDatabaseContract.MovieEntry.COLUMN_DATE_ADDED_TO_FAVORITES + " DESC");
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished: " + loader.getId());
        switch (loader.getId()) {
            case POPULAR_MOVIES_LOADER_ID:
                Log.d(TAG, "pop mov");
                if (data.getCount() == 0) {
                    movieRepository.initialRemoteFetch();
                    showProgress();
                    return;
                }
                popularMoviesCursor = data;
                if (currentTab == POPULAR) moviesAdapter.swapCursor(data);
                break;
            case TOP_RATED_MOVIES_LOADER_ID:
                Log.d(TAG, "top rated mov");
                topRatedMoviesCursor = data;
                if (currentTab == TOP_RATED) moviesAdapter.swapCursor(data);
                break;
            case FAVORITE_MOVIES_LOADER_ID:
                Log.d(TAG, "fav mov");
                favoriteMovieCursor = data;
                if (currentTab == FAVORITE) moviesAdapter.swapCursor(data);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
        hideNoConnectionMessage();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private class FetchingStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(Constants.INTENT_SERVICE_BROADCAST_MESSAGE);
            switch (message) {
                case Constants.FETCH_POPULAR_MOVIES_FINISHED_MESSAGE:
                    Log.d(TAG, "Fetched popular movies");
                    hideProgress();
                    break;
                case Constants.FETCH_TOP_RATED_MOVIES_FINISHED_MESSAGE:
                    Log.d(TAG, "Fetched top rated movies");
                    hideProgress();
                    break;
                case Constants.NO_CONNECTION_MESSAGE:
                    Log.d(TAG, "No connection available");
                    showNoConnectionMessage();
                    hideProgress();
                    break;
            }
        }
    }
}
