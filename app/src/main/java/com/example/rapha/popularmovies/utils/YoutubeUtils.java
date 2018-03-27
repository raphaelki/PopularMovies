package com.example.rapha.popularmovies.utils;

import android.net.Uri;

public class YoutubeUtils {

    public static Uri getImageUrlFromKey(String youtubeKey) {
        return Uri.parse(Constants.YOUTUBE_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(youtubeKey)
                .appendPath(Constants.YOUTUBE_IMAGE_FILENAME)
                .build();
    }

    public static Uri getYoutubeAppUri(String youtubeKey) {
        return Uri.parse(Constants.YOUTUBE_APP_LINK_BASE_URI + youtubeKey);
    }

    public static Uri getYoutubeVideoURL(String youtubeKey) {
        return Uri.parse(Constants.YOUTUBE_BASE_VIDEO_URL + youtubeKey);
    }
}
