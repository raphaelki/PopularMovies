package com.example.rapha.popularmovies.details;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Cursor cursor;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.bind(cursor);
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            Log.d(TAG, "Cursor swapped: " + newCursor.getCount() + " reviews");
            cursor = newCursor;
        }
    }

    @Override
    public int getItemCount() {
        if (cursor != null) return cursor.getCount();
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ReviewViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Cursor cursor) {
            TextView contentTv = itemView.findViewById(R.id.review_content);
            TextView authorTv = itemView.findViewById(R.id.review_author);
            contentTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.ReviewEntry.COLUMN_CONTENT)));
            authorTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.ReviewEntry.COLUMN_AUTHOR)));
        }
    }
}
