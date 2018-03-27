package com.example.rapha.popularmovies.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.utils.Constants;

public class MovieDetailsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) fragmentManager.findFragmentById(R.id.detail_fragment_frame);

        if (movieDetailFragment == null) {
            movieDetailFragment = new MovieDetailFragment();
            fragmentManager.beginTransaction().add(R.id.detail_fragment_frame, movieDetailFragment).commit();
            int detailsMovieId = getIntent().getIntExtra(Constants.MOVIE_ID_BUNDLE_KEY, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.MOVIE_ID_BUNDLE_KEY, detailsMovieId);
            movieDetailFragment.setArguments(bundle);
        }
    }
}
