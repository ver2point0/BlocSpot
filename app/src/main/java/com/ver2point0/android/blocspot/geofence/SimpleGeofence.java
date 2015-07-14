package com.ver2point0.android.blocspot.geofence;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {

    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;

    public SimpleGeofence(String id, double latitude, double longitude, float radius, long expirationDuration, int transitionType) {
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
        mExpirationDuration = expirationDuration;
        mTransitionType = transitionType;
    }

    public String getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getRadius() {
        return mRadius;
    }

    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    public Geofence toGeofence() {
        return new Geofence.Builder()
                .setRequestId(getId())
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(mExpirationDuration)
                .build();
    }
}
