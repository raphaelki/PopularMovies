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

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
