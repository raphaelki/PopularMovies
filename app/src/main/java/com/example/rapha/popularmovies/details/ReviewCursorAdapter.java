package com.example.rapha.popularmovies.details;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;

public class ReviewCursorAdapter extends CursorAdapter {
    public ReviewCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.review_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contentTv = view.findViewById(R.id.review_content);
        TextView authorTv = view.findViewById(R.id.review_author);

        contentTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.ReviewEntry.COLUMN_CONTENT)));
        authorTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.ReviewEntry.COLUMN_AUTHOR)));
    }
}
