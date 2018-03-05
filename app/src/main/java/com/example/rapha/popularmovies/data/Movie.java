package com.example.rapha.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String title;
    private String posterURL;
    private String originalTitle;
    private String plot;
    private String releaseDate;
    private String userRating;

    public Movie(String title, String posterURL, String originalTitle, String plot, String releaseDate, String userRating) {
        this.title = title;
        this.posterURL = posterURL;
        this.originalTitle = originalTitle;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
    }

    private Movie(Parcel in) {
        title = in.readString();
        posterURL = in.readString();
        originalTitle = in.readString();
        plot = in.readString();
        releaseDate = in.readString();
        userRating = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPlot() {
        return plot;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getUserRating() {
        return userRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterURL);
        dest.writeString(originalTitle);
        dest.writeString(plot);
        dest.writeString(releaseDate);
        dest.writeString(userRating);
    }
}
