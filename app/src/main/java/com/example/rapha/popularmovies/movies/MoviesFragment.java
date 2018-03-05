package com.example.rapha.popularmovies.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.details.MovieDetailsActivity;
import com.example.rapha.popularmovies.utils.MovieDbJsonUtils;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MoviesFragment extends Fragment implements MoviesAdapter.OnGridItemClickedHandler {

    private final int GRID_COLUMN_SPAN = 2;
    private final String TAG = getClass().getSimpleName();
    private MoviesAdapter moviesAdapter;
    private RecyclerView posterRv;
    private ProgressBar loadingPb;
    private int currentPage = 1;
    private String sortOrder = MovieDbNetworkUtils.POPULAR_PATH;

    public MoviesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        posterRv = view.findViewById(R.id.posters_rv);
        loadingPb = view.findViewById(R.id.fetching_data_pb);
        moviesAdapter = new MoviesAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), GRID_COLUMN_SPAN);
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

        loadData();

        return view;
    }

    public void setSortOrder(String sortOrder) {
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
        Log.d(TAG, "current page is set to " + currentPage);
    }

    private void loadData() {
        String apiKey = getString(R.string.api_key);
        new MovieDbQueryTask().execute(apiKey, String.valueOf(currentPage), sortOrder);
    }

    private void showProgressBar() {
        loadingPb.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPb.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(Movie movie) {
        Log.d(TAG, "Selected movie: " + movie.getTitle());
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    class MovieDbQueryTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (currentPage == 1) {
                moviesAdapter.swapMovies(movies);
            } else {
                moviesAdapter.appendMovieList(movies);
            }
            hideProgressBar();
            incrementCurrentPage();
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            String apiKey = strings[0];
            String pageToLoad = strings[1];
            String sortOrder = strings[2];
            try {
                String jsonData = MovieDbNetworkUtils.fetchMovies(apiKey, pageToLoad, sortOrder);
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
            showProgressBar();
        }
    }
}
