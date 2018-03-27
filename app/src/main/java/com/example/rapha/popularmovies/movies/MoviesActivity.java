package com.example.rapha.popularmovies.movies;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.rapha.popularmovies.R;

public class MoviesActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private MoviesFragment moviesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        FragmentManager fragmentManager = getSupportFragmentManager();
        moviesFragment = (MoviesFragment) fragmentManager.findFragmentById(R.id.movies_fragment_frame);
        if (moviesFragment == null) {
            moviesFragment = new MoviesFragment();
            fragmentManager.beginTransaction().add(R.id.movies_fragment_frame, moviesFragment).commit();
        }
    }
}