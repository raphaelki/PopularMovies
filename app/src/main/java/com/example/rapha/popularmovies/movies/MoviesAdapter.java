package com.example.rapha.popularmovies.movies;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.models.Movie;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.TmdbUtils;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<Movie> movies;
    private OnGridItemClickedHandler onGridItemClickedHandler;
    private Cursor cursor;

    public MoviesAdapter(List<Movie> movies, OnGridItemClickedHandler onGridItemClickedHandler) {
        this.movies = movies;
        this.onGridItemClickedHandler = onGridItemClickedHandler;
    }

    public MoviesAdapter(OnGridItemClickedHandler onGridItemClickedHandler) {
        this.onGridItemClickedHandler = onGridItemClickedHandler;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {
        //holder.bind(movies.get(position));
        cursor.moveToPosition(position);
        holder.bind(cursor);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    interface OnGridItemClickedHandler {
        void onItemClicked(int movieId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Cursor cursor) {
            ImageView posterIv = itemView.findViewById(R.id.poster_item_iv);
            String posterPath = cursor.getString(MoviesFragment.INDEX_MOVIE_POSTER_PATH);
            String title = cursor.getString(MoviesFragment.INDEX_MOVIE_TITLE);
            posterIv.setContentDescription(itemView.getContext().getString(R.string.content_description) + title);
            GlideApp.with(itemView.getContext())
                    .load(TmdbUtils.getFullImageURL(posterPath))
                    .placeholder(R.drawable.placeholder)
                    .into(posterIv);
        }

        @Override
        public void onClick(View v) {
            cursor.moveToPosition(getAdapterPosition());
            onGridItemClickedHandler.onItemClicked(cursor.getInt(MoviesFragment.INDEX_MOVIE_ID));
        }
    }
}
