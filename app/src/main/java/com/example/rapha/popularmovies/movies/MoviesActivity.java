package com.example.rapha.popularmovies.movies;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rapha.popularmovies.R;

public class MoviesActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private MoviesFragment moviesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        FragmentManager fragmentManager = getSupportFragmentManager();
        moviesFragment = (MoviesFragment) fragmentManager.findFragmentById(R.id.movies_fragment_frame);
        if (moviesFragment == null) {
            moviesFragment = new MoviesFragment();
            fragmentManager.beginTransaction().add(R.id.movies_fragment_frame, moviesFragment).commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.movie_overview_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.sort_order_spinner);
//        Spinner spinner = (Spinner) menuItem.getActionView();
//        spinner.setSelection(spinnerPosition);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = (String) parent.getItemAtPosition(position);
//                if (isUserInteracting) {
//                    spinnerPosition = position;
//                    Log.d(TAG, "Sort order selected: " + item);
//                    String popularSortOrder = getString(R.string.menu_popular);
//                    String topRatedSortOrder = getString(R.string.menu_top_rated);
//                    if (item.equals(popularSortOrder)) {
//                        moviesFragment.setSortOrder(MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY);
//                    } else if (item.equals(topRatedSortOrder)) {
//                        moviesFragment.setSortOrder(MoviesDatabaseContract.MovieEntry.COLUMN_RATING);
//                    } else {
//                        throw new RuntimeException("Sort order not implemented: " + item);
//                    }
//                    isUserInteracting = false;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//        return true;
//    }

//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//        isUserInteracting = true;
//    }
}
