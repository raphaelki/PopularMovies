package com.example.rapha.popularmovies.listener;

public interface AsyncTaskListener<T> {

    void onCompletion(T result);
}
