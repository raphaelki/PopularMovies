package com.example.rapha.popularmovies.movies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.utils.MovieDbJsonUtils;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MoviesFragment extends Fragment implements MoviesAdapter.OnGridItemClickedHandler {

    private final String TAG = getClass().getSimpleName();

    private MoviesAdapter moviesAdapter;
    private TextView noConnectionTv;
    private RecyclerView posterRv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 1;
    private String sortOrder = MovieDbNetworkUtils.POPULAR_PATH;
    private String apiKey;

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

        apiKey = getString(R.string.api_key);

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
                    Log.d(TAG, "Loading additional data");
                    loadData();
                }
            }
        });

        return view;
    }

    public void setSortOrder(String sortOrder) {
        Log.d(TAG, "Setting sort order to: " + sortOrder);
        this.sortOrder = sortOrder;
        moviesAdapter.clearMovieList();
        resetCurrentPage();
        loadData();
    }

    private void resetCurrentPage() {
        currentPage = 1;
    }

    private void incrementCurrentPage() {
        currentPage++;
        Log.d(TAG, "Current page is incremented to " + currentPage);
    }

    private void loadData() {
        new MovieDbQueryTask().execute();
    }

    private void showProgress() {
        swipeRefreshLayout.setRefreshing(true);
        noConnectionTv.setVisibility(View.GONE);
    }

    private void hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showNoConnectionMessage() {
        noConnectionTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(Movie movie) {
        Log.d(TAG, "Selected movie: " + movie.getTitle());
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra(getString(R.string.movie_parcelable_key), movie);
        startActivity(intent);
    }

    class MovieDbQueryTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies == null) {
                showNoConnectionMessage();
            } else {
                if (currentPage == 1) {
                    moviesAdapter.swapMovies(movies);
                } else {
                    moviesAdapter.appendMovieList(movies);
                }
                incrementCurrentPage();
            }
            hideProgress();
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            try {
                String jsonData = MovieDbNetworkUtils.fetchMovies(getContext(), apiKey, String.valueOf(currentPage), sortOrder);
                try {
                    return MovieDbJsonUtils.parseMovieDbJson(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }
    }
}
