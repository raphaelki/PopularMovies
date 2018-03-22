package com.example.rapha.popularmovies.details;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.YoutubeUtils;


public class TrailerCursorAdapter extends CursorAdapter {

    public TrailerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.trailer_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = view.findViewById(R.id.trailer_title);
        ImageView thumbnailImageView = view.findViewById(R.id.trailer_iv);
        textView.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.TrailerEntry.COLUMN_TITLE)));
        String youtubeKey = cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.TrailerEntry.COLUMN_YOUTUBE_KEY));
        Uri youtubeURL = YoutubeUtils.getImageUrlFromKey(youtubeKey);
        GlideApp.with(view.getContext()).load(youtubeURL).placeholder(R.drawable.ic_placeholder_trailer).into(thumbnailImageView);
    }
}
