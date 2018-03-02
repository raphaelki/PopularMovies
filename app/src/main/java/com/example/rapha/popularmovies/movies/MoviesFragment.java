package com.example.rapha.popularmovies.movies;

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
import com.example.rapha.popularmovies.utils.MovieDbJsonUtils;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MoviesFragment extends Fragment {

    private final int GRID_COLUMN_SPAN = 2;
    private final String TAG = getClass().getSimpleName();
    private MoviesAdapter moviesAdapter;
    private RecyclerView posterRv;
    private ProgressBar loadingPb;
    private int currentPage = 1;

    public MoviesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        posterRv = view.findViewById(R.id.posters_rv);
        loadingPb = view.findViewById(R.id.fetching_data_pb);
        moviesAdapter = new MoviesAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), GRID_COLUMN_SPAN);
        posterRv.setLayoutManager(layoutManager);
        posterRv.setAdapter(moviesAdapter);

        posterRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    Log.d(TAG, "loading additional data");
                    loadData();
                }
            }
        });

        loadData();

        return view;
    }

    private void incrementCurrentPage() {
        currentPage++;
        Log.d(TAG, "current page is set to " + currentPage);
    }

    private void loadData() {
        String apiKey = getString(R.string.api_key);
        new MovieDbQueryTask().execute(apiKey, String.valueOf(currentPage));
    }

    private void showLoadingStatus() {
        loadingPb.setVisibility(View.VISIBLE);
    }

    private void showPosterGrid() {
        loadingPb.setVisibility(View.GONE);
    }

    class MovieDbQueryTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (currentPage == 1) {
                moviesAdapter.swapMovies(movies);
            } else {
                moviesAdapter.appendMovieList(movies);
            }
            showPosterGrid();
            incrementCurrentPage();
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            String apiKey = strings[0];
            String pageToLoad = strings[1];
            try {
                String jsonData = MovieDbNetworkUtils.fetchPopularMovies(apiKey, pageToLoad);
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
            showLoadingStatus();
        }
    }
}
