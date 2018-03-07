package com.example.rapha.popularmovies.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private boolean movieIsFavorite = false;

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
            Movie movie = getIntent().getParcelableExtra(getString(R.string.movie_parcelable_key));
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.movie_parcelable_key), movie);
            movieDetailFragment.setArguments(bundle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    private void onFavoriteButtonClicked(MenuItem item) {
        if (!movieIsFavorite) {
            item.setIcon(R.drawable.ic_favorite);
            Toast.makeText(this, getString(R.string.add_to_favorites_toast), Toast.LENGTH_LONG).show();
            movieIsFavorite = true;
        } else {
            item.setIcon(R.drawable.ic_favorite_border);
            movieIsFavorite = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.favorite_action) {
            onFavoriteButtonClicked(item);
        }

        return super.onOptionsItemSelected(item);
    }
}
