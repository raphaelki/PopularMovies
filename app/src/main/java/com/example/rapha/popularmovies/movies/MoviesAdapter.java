package com.example.rapha.popularmovies.movies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<Movie> movies;
    private OnGridItemClickedHandler onGridItemClickedHandler;

    public MoviesAdapter(List<Movie> movies, OnGridItemClickedHandler onGridItemClickedHandler) {
        this.movies = movies;
        this.onGridItemClickedHandler = onGridItemClickedHandler;
    }

    public MoviesAdapter(OnGridItemClickedHandler onGridItemClickedHandler) {
        this(new ArrayList<Movie>(), onGridItemClickedHandler);
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void swapMovies(List<Movie> movies) {
        if (movies != null) {
            this.movies = movies;
            notifyDataSetChanged();
        }
    }

    public void appendMovieList(List<Movie> movies) {
        if (movies != null) {
            this.movies.addAll(movies);
            notifyDataSetChanged();
        }
    }

    public void clearMovieList() {
        movies.clear();
        notifyDataSetChanged();
    }

    interface OnGridItemClickedHandler {
        void onItemClicked(Movie movie);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Movie movie;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            this.movie = movie;
            ImageView posterIv = itemView.findViewById(R.id.poster_item_iv);
            posterIv.setContentDescription(itemView.getContext().getString(R.string.content_description) + movie.getTitle());
            Log.d(TAG, "Loading poster with glide from url: " + movie.getPosterURL());
            GlideApp.with(itemView.getContext())
                    .load(movie.getPosterURL())
                    .placeholder(R.drawable.placeholder)
                    .into(posterIv);
        }

        @Override
        public void onClick(View v) {
            onGridItemClickedHandler.onItemClicked(movie);
        }
    }
}
