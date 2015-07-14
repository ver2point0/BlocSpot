package com.ver2point0.android.blocspot.geofence;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;

import java.util.List;

public class EditGeofences implements GeofencingApi {

    private Context mContext;

    public EditGeofences(Context context) {
        mContext = context;
    }

    @Override
    public PendingResult<Status> addGeofences(GoogleApiClient googleApiClient, List<Geofence> geofences, PendingIntent pendingIntent) {
        return null;
    }

    @Override
    public PendingResult<Status> addGeofences(GoogleApiClient googleApiClient, GeofencingRequest geofencingRequest, PendingIntent pendingIntent) {
        return null;
    }

    @Override
    public PendingResult<Status> removeGeofences(GoogleApiClient googleApiClient, PendingIntent pendingIntent) {
        return null;
    }

    @Override
    public PendingResult<Status> removeGeofences(GoogleApiClient googleApiClient, List<String> strings) {
        return null;
    }
}

