package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {

    @SerializedName("id")
    private Integer id;
    @SerializedName("results")
    private List<Video> videos = null;

    public Integer getId() {
        return id;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
