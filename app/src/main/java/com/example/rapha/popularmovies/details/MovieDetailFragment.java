package com.example.rapha.popularmovies.details;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MovieRepository;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.TmdbUtils;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();
    private final int DETAIL_LOADER_ID = 13452;
    private final int TRAILER_LOADER_ID = 23429;
    private final int REVIEW_LOADER_ID = 23430;
    private TextView titleTv;
    private TextView originalTitleTv;
    private ImageView posterIv;
    private TextView yearTv;
    private TextView ratingTv;
    private TextView plotTv;
    private RecyclerView trailerRv;
    private RecyclerView reviewRv;
    private LinearLayoutManager trailerLayoutManager;
    private LinearLayoutManager reviewLinearLayoutManager;
    private ImageView toolbarIv;
    private Toolbar toolbar;
    private FloatingActionButton favoriteButton;
    private ActionBar supportActionBar;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View trailersSection;
    private View reviewSection;

    private boolean isFavorite;
    private int movieId;
    private MovieRepository movieRepository;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        movieRepository = MovieRepository.getInstance(getContext());
        super.onCreate(savedInstanceState);
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
        posterIv = view.findViewById(R.id.detail_poster_iv);
        trailerRv = view.findViewById(R.id.detail_trailer_rv);
        reviewRv = view.findViewById(R.id.detail_review_rv);
        collapsingToolbarLayout = view.findViewById(R.id.detail_collapsing_toolbar_layout);
        toolbarIv = view.findViewById(R.id.toolbar_iv);
        toolbar = view.findViewById(R.id.toolbar);
        favoriteButton = view.findViewById(R.id.favorite_action_button);

        trailersSection = view.findViewById(R.id.detail_trailer_section);
        reviewSection = view.findViewById(R.id.detail_review_section);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                movieRepository.changeMovieFavoriteStatus(movieId, isFavorite);
                setFavoriteButtonIcon();
            }
        });

        movieId = getArguments().getInt("movie_id");

        trailerAdapter = new TrailerAdapter();
        trailerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trailerRv.setLayoutManager(trailerLayoutManager);
        trailerRv.setHasFixedSize(true);
        trailerRv.setAdapter(trailerAdapter);

        reviewLinearLayoutManager = new LinearLayoutManager(getContext());
        reviewAdapter = new ReviewAdapter();
        reviewRv.setAdapter(reviewAdapter);
        reviewRv.setLayoutManager(reviewLinearLayoutManager);
        reviewRv.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(reviewRv.getContext(), reviewLinearLayoutManager.getOrientation());
        reviewRv.addItemDecoration(dividerItemDecoration);

        getActivity().getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle("");
    }

    private void populateView(Cursor cursor) {
        Log.d(TAG, "populateView");
        String title = cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_TITLE));

        titleTv.setText(title);
        originalTitleTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
        yearTv.setText(TmdbUtils.convertTmdbDateToLocalDateFormat(getContext(), cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE))));
        ratingTv.setText(getString(R.string.detail_rating, String.valueOf(cursor.getDouble(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_RATING)))));
        plotTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_OVERVIEW)));
        isFavorite = cursor.getInt(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE)) == 1;
        setFavoriteButtonIcon();
        collapsingToolbarLayout.setTitle(title);
        String fullPosterPath = TmdbUtils.getFullImageURL(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH)));
        GlideApp.with(getContext()).load(fullPosterPath).placeholder(R.drawable.placeholder).into(posterIv);
        GlideApp.with(getContext()).load(fullPosterPath).placeholder(R.drawable.ic_placeholder_trailer).into(toolbarIv);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case DETAIL_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.MovieEntry.buildMovieEntryUri(movieId),
                        null,
                        null,
                        null,
                        null);
            case TRAILER_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.TrailerEntry.buildTrailerEntryUri(movieId),
                        null,
                        null,
                        null,
                        null);
            case REVIEW_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.ReviewEntry.buildReviewEntryUri(movieId),
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case DETAIL_LOADER_ID:
                if (cursor != null && cursor.moveToFirst()) populateView(cursor);
                break;
            case TRAILER_LOADER_ID:
                if (cursor != null) {
                    Log.d(TAG, cursor.getCount() + " trailers loaded from database");

                    if (cursor.getCount() == 0) {
                        trailersSection.setVisibility(View.GONE);
                        movieRepository.fetchTrailers(movieId);
                    } else {
                        cursor.moveToFirst();
                        trailersSection.setVisibility(View.VISIBLE);
                    }
                    trailerAdapter.swapCursor(cursor);
                }
                break;
            case REVIEW_LOADER_ID:
                if (cursor != null) {
                    Log.d(TAG, cursor.getCount() + " reviews loaded from database");
                    if (cursor.getCount() == 0) {
                        movieRepository.fetchReviews(movieId);
                        reviewSection.setVisibility(View.GONE);
                    } else {
                        cursor.moveToFirst();
                        reviewSection.setVisibility(View.VISIBLE);
                    }
                    reviewAdapter.swapCursor(cursor);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private void setFavoriteButtonIcon() {
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }
}
