package com.example.rapha.popularmovies.movies;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.TmdbUtils;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private final OnGridItemClickedHandler onGridItemClickedHandler;
    private Cursor cursor;

    public MoviesAdapter(OnGridItemClickedHandler onGridItemClickedHandler) {
        this.onGridItemClickedHandler = onGridItemClickedHandler;
    }

    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {
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
            Log.d(TAG, "Cursor swapped");
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
            ImageView favoriteIv = itemView.findViewById(R.id.favorite_iv);
            Boolean isFavorite = cursor.getInt(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE)) == 1;
            favoriteIv.setVisibility(isFavorite ? View.VISIBLE : View.GONE);
            String posterPath = cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH));
            String title = cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_TITLE));
            posterIv.setContentDescription(itemView.getContext().getString(R.string.content_description) + title);
            GlideApp.with(itemView.getContext())
                    .load(TmdbUtils.getFullImageURL(posterPath))
                    .placeholder(R.drawable.ic_placeholder)
                    .into(posterIv);
        }

        @Override
        public void onClick(View v) {
            cursor.moveToPosition(getAdapterPosition());
            onGridItemClickedHandler.onItemClicked(cursor.getInt(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry._ID)));
        }
    }
}
