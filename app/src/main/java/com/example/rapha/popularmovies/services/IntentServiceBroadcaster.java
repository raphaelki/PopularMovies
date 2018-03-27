package com.example.rapha.popularmovies.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.rapha.popularmovies.utils.Constants;

public class IntentServiceBroadcaster {

    private final LocalBroadcastManager broadcastManager;

    public IntentServiceBroadcaster(Context context) {
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void fireBroadcastEvent(String message) {
        Intent broadcastIntent = new Intent(Constants.INTENT_SERVICE_BROADCAST_ACTION);
        broadcastIntent.putExtra(Constants.INTENT_SERVICE_BROADCAST_MESSAGE, message);
        broadcastManager.sendBroadcast(broadcastIntent);
    }
}
