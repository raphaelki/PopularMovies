package com.example.rapha.popularmovies.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TmdbUtils {

    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";
    private static final String TMDB_DATE_PATTERN = "yyyy-MM-dd";

    public static String getFullImageURL(String posterPath) {
        return IMAGE_URL + posterPath;
    }

    public static String convertTmdbDateToLocalDateFormat(Context context, String tmdbDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TMDB_DATE_PATTERN);
        String dateShown;
        try {
            Date date = simpleDateFormat.parse(tmdbDate);
            dateShown = DateFormat.getDateInstance(DateFormat.SHORT, context.getResources().getConfiguration().locale).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            dateShown = tmdbDate;
        }
        return dateShown;
    }
}
