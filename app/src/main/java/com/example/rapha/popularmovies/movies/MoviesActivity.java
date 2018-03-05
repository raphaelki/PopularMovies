package com.example.rapha.popularmovies.movies;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

public class MoviesActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private MoviesFragment moviesOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        FragmentManager fragmentManager = getSupportFragmentManager();
        moviesOverview = new MoviesFragment();
        fragmentManager.beginTransaction().add(R.id.movies_fragment_frame, moviesOverview).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_overview_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.sort_order_spinner);
        Spinner spinner = (Spinner) menuItem.getActionView();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "Sort order selected: " + item);
                String popularSortOrder = getString(R.string.menu_popular);
                String topRatedSortOrder = getString(R.string.menu_top_rated);
                if (item.equals(popularSortOrder)) {
                    moviesOverview.setSortOrder(MovieDbNetworkUtils.POPULAR_PATH);
                } else if (item.equals(topRatedSortOrder)) {
                    moviesOverview.setSortOrder(MovieDbNetworkUtils.TOP_RATED_PATH);
                } else {
                    throw new RuntimeException("Sort order not implemented: " + item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return true;
    }
}
