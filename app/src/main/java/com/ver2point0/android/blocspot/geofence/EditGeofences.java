package com.ver2point0.android.blocspot.geofence;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.ver2point0.android.blocspot.ui.activity.BlocSpotActivity;

import java.util.List;

public class EditGeofences implements GeofencingApi {

    private Context mContext;
    private Boolean mInProgress;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;

    public EditGeofences(Context context) {
        mContext = context;
        mGoogleApiClient = null;
        mPendingIntent = null;
        mInProgress = false;
    }

    public void setInProgressFlag(boolean flag) {
        mInProgress = flag;
    }

    public boolean getInProgressFlag() {
        return mInProgress;
    }

    @Override
    public PendingResult<Status> addGeofences(GoogleApiClient googleApiClient, List<Geofence> geofences, PendingIntent pendingIntent) {
        return null;
    }

    @Override
    public PendingResult<Status> addGeofences(GoogleApiClient googleApiClient, GeofencingRequest geofencingRequest, PendingIntent pendingIntent) {
        if (!((BlocSpotActivity) mContext).servicesConnected()) {
            return null;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) mContext)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) mContext)
                .build();

        if (!mInProgress) {
            mInProgress = true;
            mGoogleApiClient.connect();
        }
        else {
        }

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

