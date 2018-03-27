package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("key")
    private String key;
    @SerializedName("id")
    private String id;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }
}
