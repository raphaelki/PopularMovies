package com.example.rapha.popularmovies.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;

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
            Movie movie = getIntent().getParcelableExtra("movie");
            setTitle(movie.getTitle());
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movie);
            movieDetailFragment.setArguments(bundle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }
}
