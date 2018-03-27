package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {

    @SerializedName("results")
    private final List<Video> videos = null;
    @SerializedName("id")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
