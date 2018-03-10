package com.example.rapha.popularmovies.movies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.listener.AsyncTaskListener;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment implements MoviesAdapter.OnGridItemClickedHandler {

    private final static String API_KEY = BuildConfig.TMDB_API_KEY;
    private final String TAG = getClass().getSimpleName();
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
    private String sortOrder = MovieDbNetworkUtils.POPULAR_PATH;

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

        return view;
    }

    private void restoreViewState(Bundle savedInstanceState) {
        pageToLoad = savedInstanceState.getInt(PAGE_TO_LOAD_KEY);
        sortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
        List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        moviesAdapter.swapMovies(movies);
        Parcelable rvState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE_KEY);
        posterRv.getLayoutManager().onRestoreInstanceState(rvState);
        noConnectionTv.setVisibility(savedInstanceState.getInt(NO_CONNECTION_VISIBILITY_KEY));
    }

    public void setSortOrder(String sortOrder) {
        Log.d(TAG, "Setting sort order to: " + sortOrder);
        this.sortOrder = sortOrder;
        moviesAdapter.clearMovieList();
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
        new FetchMoviesFromTmdbTask(getContext(), new FetchMoviesListener()).execute(String.valueOf(pageToLoad), sortOrder);
        Log.d(TAG, "Loading movies page " + pageToLoad);
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
    }

    @Override
    public void onItemClicked(Movie movie) {
        Log.d(TAG, "Selected movie: " + movie.getTitle());
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra(getString(R.string.movie_parcelable_key), movie);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_TO_LOAD_KEY, pageToLoad);
        outState.putString(SORT_ORDER_KEY, sortOrder);
        Parcelable rvState = posterRv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE_KEY, rvState);
        outState.putParcelableArrayList(MOVIES_KEY, (ArrayList<? extends Parcelable>) moviesAdapter.getMovies());
        outState.putInt(NO_CONNECTION_VISIBILITY_KEY, noConnectionTv.getVisibility());
    }

    class FetchMoviesListener implements AsyncTaskListener<List<Movie>> {

        @Override
        public void onCompletion(List<Movie> movies) {
            if (movies == null) {
                showNoConnectionMessage();
            } else {
                if (pageToLoad == 1) {
                    moviesAdapter.swapMovies(movies);
                } else {
                    moviesAdapter.appendMovieList(movies);
                }
                incrementCurrentPage();
            }
            hideProgress();
        }
    }
}
