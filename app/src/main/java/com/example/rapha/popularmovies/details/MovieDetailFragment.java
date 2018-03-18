package com.example.rapha.popularmovies.details;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MoviesDatabaseContract;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.TmdbUtils;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] DETAIL_MOVIE_PROJECTION = {
            MoviesDatabaseContract.MovieEntry._ID,
            MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesDatabaseContract.MovieEntry.COLUMN_TITLE,
            MoviesDatabaseContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesDatabaseContract.MovieEntry.COLUMN_RATING,
            MoviesDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesDatabaseContract.MovieEntry.COLUMN_RUNTIME
    };
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    public static final int INDEX_MOVIE_OVERVIEW = 3;
    public static final int INDEX_MOVIE_RELEASE_DATE = 4;
    public static final int INDEX_MOVIE_RATING = 5;
    public static final int INDEX_MOVIE_ORIGINAL_TITLE = 6;
    public static final int INDEX_MOVIE_RUNTIME = 7;
    private final String TAG = getClass().getSimpleName();
    private final int DETAIL_LOADER_ID = 13452;
    private TextView titleTv;
    private TextView originalTitleTv;
    private ImageView posterIv;
    private TextView yearTv;
    private TextView ratingTv;
    private TextView plotTv;
    private Uri movieUri;

    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        titleTv = view.findViewById(R.id.detail_title);
        originalTitleTv = view.findViewById(R.id.detail_original_title);
        plotTv = view.findViewById(R.id.detail_plot);
        ratingTv = view.findViewById(R.id.detail_rating);
        yearTv = view.findViewById(R.id.detail_year);
        posterIv = view.findViewById(R.id.detaill_poster_iv);

        movieUri = getArguments().getParcelable("movie_uri");
        if (movieUri != null) {
            getActivity().getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        }
        return view;
    }

    private void populateView(Cursor cursor) {
        titleTv.setText(cursor.getString(INDEX_MOVIE_TITLE));
        originalTitleTv.setText(cursor.getString(INDEX_MOVIE_ORIGINAL_TITLE));
        yearTv.setText(TmdbUtils.convertTmdbDateToLocalDateFormat(getContext(), cursor.getString(INDEX_MOVIE_RELEASE_DATE)));
        ratingTv.setText(getString(R.string.detail_rating, String.valueOf(cursor.getDouble(INDEX_MOVIE_RATING))));
        plotTv.setText(cursor.getString(INDEX_MOVIE_OVERVIEW));
        String fullPosterPath = TmdbUtils.getFullImageURL(cursor.getString(INDEX_MOVIE_POSTER_PATH));
        GlideApp.with(getContext()).load(fullPosterPath).placeholder(R.drawable.placeholder).into(posterIv);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case DETAIL_LOADER_ID:
                return new CursorLoader(getContext(),
                        movieUri,
                        DETAIL_MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Unknwon loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) populateView(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
