package com.example.rapha.popularmovies.utils;

import android.net.Uri;

public class YoutubeUtils {

    private static final String YOUTUBE_IMAGE_BASE_URL = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_FILENAME = "mqdefault.jpg";

    public static Uri getImageUrlFromKey(String youtubeKey) {
        return Uri.parse(YOUTUBE_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(youtubeKey)
                .appendPath(YOUTUBE_IMAGE_FILENAME)
                .build();
    }
}
