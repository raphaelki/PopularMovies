package com.example.rapha.popularmovies.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.TmdbUtils;

public class MovieDetailFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private TextView titleTv;
    private TextView originalTitleTv;
    private ImageView posterIv;
    private TextView yearTv;
    private TextView ratingTv;
    private TextView plotTv;

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

        Movie movie = getArguments().getParcelable(getString(R.string.movie_parcelable_key));
        if (movie != null) {
            populateView(movie);
        }
        return view;
    }


    private void populateView(Movie movie) {
        titleTv.setText(movie.getTitle());
        originalTitleTv.setText(movie.getOriginalTitle());
        yearTv.setText(TmdbUtils.convertTmdbDateToLocalDateFormat(getContext(), movie.getReleaseDate()));
        ratingTv.setText(getString(R.string.detail_rating, String.valueOf(movie.getVoteAverage())));
        plotTv.setText(movie.getOverview());
        String fullPosterPath = TmdbUtils.getFullImageURL(movie.getPosterPath());
        GlideApp.with(getContext()).load(fullPosterPath).placeholder(R.drawable.placeholder).into(posterIv);
    }
}
