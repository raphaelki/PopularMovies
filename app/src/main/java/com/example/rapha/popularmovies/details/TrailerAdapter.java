package com.example.rapha.popularmovies.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.YoutubeUtils;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Cursor cursor;

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.bind(cursor);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) return cursor.getCount();
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null) {
            Log.d(TAG, "Cursor swapped");
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        private String youtubeKey;

        public TrailerViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final Cursor cursor) {
            TextView textView = itemView.findViewById(R.id.trailer_title);
            ImageView thumbnailImageView = itemView.findViewById(R.id.trailer_iv);
            textView.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.TrailerEntry.COLUMN_TITLE)));
            youtubeKey = cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.TrailerEntry.COLUMN_YOUTUBE_KEY));
            Uri youtubeURL = YoutubeUtils.getImageUrlFromKey(youtubeKey);
            GlideApp.with(itemView.getContext()).load(youtubeURL).placeholder(R.drawable.ic_placeholder_trailer).into(thumbnailImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playTrailerInYoutubeApp();
                }
            });
        }

        private void playTrailerInYoutubeApp() {
            Log.d(TAG, "Playing trailer:" + youtubeKey);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, YoutubeUtils.getYoutubeAppUri(youtubeKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, YoutubeUtils.getYoutubeVideoURL(youtubeKey));
            try {
                itemView.getContext().startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                itemView.getContext().startActivity(webIntent);
            }
        }
    }
}
