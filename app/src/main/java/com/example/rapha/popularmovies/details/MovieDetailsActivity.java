package com.example.rapha.popularmovies.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rapha.popularmovies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) fragmentManager.findFragmentById(R.id.detail_fragment_frame);

        if (movieDetailFragment == null) {
            movieDetailFragment = new MovieDetailFragment();
            fragmentManager.beginTransaction().add(R.id.detail_fragment_frame, movieDetailFragment).commit();
            int detailsMovieId = getIntent().getIntExtra("movie_id", 0);
            Bundle bundle = new Bundle();
            bundle.putInt("movie_id", detailsMovieId);
            movieDetailFragment.setArguments(bundle);
        }
    }
}
