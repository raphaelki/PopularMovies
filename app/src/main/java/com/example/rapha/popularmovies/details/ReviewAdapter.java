package com.example.rapha.popularmovies.details;

import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            Log.d(TAG, "Cursor swapped");
            cursor = newCursor;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (cursor != null) return cursor.getCount();
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final String MAX_LINES = "maxLines";
        private final int EXPANSION_DURATION = 200;
        private int collapsedLines = 1;
        private boolean isExpanded = false;
        private ImageView expandIconIv;

        public ReviewViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Cursor cursor) {
            final TextView contentTv = itemView.findViewById(R.id.review_content);
            TextView authorTv = itemView.findViewById(R.id.review_author);
            contentTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.ReviewEntry.COLUMN_CONTENT)));
            authorTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.ReviewEntry.COLUMN_AUTHOR)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded) collapseTextView(contentTv);
                    else expandTextView(contentTv);
                }
            });
            expandIconIv = itemView.findViewById(R.id.review_expand_icon);
        }

        private void expandTextView(TextView tv) {
            ObjectAnimator animation = ObjectAnimator.ofInt(tv, MAX_LINES, tv.getLineCount());
            animation.setDuration(EXPANSION_DURATION).start();
            isExpanded = true;
            expandIconIv.setImageResource(R.drawable.ic_arrow_drop_up);
        }

        private void collapseTextView(TextView tv) {
            ObjectAnimator animation = ObjectAnimator.ofInt(tv, MAX_LINES, collapsedLines);
            animation.setDuration(EXPANSION_DURATION).start();
            isExpanded = false;
            expandIconIv.setImageResource(R.drawable.ic_arrow_drop_down);
        }
    }
}
