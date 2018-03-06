package com.example.rapha.popularmovies.details;

import android.content.Context;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetailFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private TextView durationTv;
    private TextView originalTitleTv;
    private ImageView posterIv;
    private TextView yearTv;
    private TextView ratingTv;
    private TextView plotTv;
    private Context context;

    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        durationTv = view.findViewById(R.id.detail_duration);
        originalTitleTv = view.findViewById(R.id.detail_original_title);
        plotTv = view.findViewById(R.id.detail_plot);
        ratingTv = view.findViewById(R.id.detail_rating);
        yearTv = view.findViewById(R.id.detail_year);
        posterIv = view.findViewById(R.id.detaill_poster_iv);

        context = view.getContext();

        Movie movie = getArguments().getParcelable("movie");
        if (movie != null) {
            populateView(movie);
        }
        return view;
    }

    private void populateView(Movie movie) {
        originalTitleTv.setText(movie.getOriginalTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateShown = movie.getReleaseDate();
        try {
            Date date = simpleDateFormat.parse(movie.getReleaseDate());
            dateShown = DateFormat.getDateInstance(DateFormat.LONG, getResources().getConfiguration().locale).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        yearTv.setText(dateShown);
        ratingTv.setText(getString(R.string.detail_rating, movie.getUserRating()));
        plotTv.setText(movie.getPlot());
        GlideApp.with(context).load(movie.getPosterURL()).placeholder(R.drawable.placeholder).into(posterIv);
    }
}
