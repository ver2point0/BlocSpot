package com.ver2point0.android.blocspot.geofence;


import android.content.Context;
import android.content.SharedPreferences;

import com.ver2point0.android.blocspot.util.Constants;

public class SimpleGeofenceStore {

    private final SharedPreferences mPrefs;

    public SimpleGeofenceStore(Context context) {
        mPrefs = context.getSharedPreferences(Constants.MAIN_PREFS, Context.MODE_PRIVATE);
    }

    public SimpleGeofence getGeofence(String id) {
        double lat = mPrefs.getFloat(getGeofenceFieldKey(id, Constants.KEY_LATITUDE),
                Constants.INVALID_FLOAT_VALUE);
        double lng = mPrefs.getFloat(getGeofenceFieldKey(id, Constants.KEY_LONGITUDE),
                Constants.INVALID_FLOAT_VALUE);
        float radius = mPrefs.getFloat(getGeofenceFieldKey(id, Constants.KEY_RADIUS),
                Constants.INVALID_FLOAT_VALUE);
        long expirationDuration = mPrefs.getLong(getGeofenceFieldKey(id, Constants.KEY_EXPIRATION_DURATION),
                Constants.INVALID_LONG_VALUE);
        int transitionType = mPrefs.getInt(getGeofenceFieldKey(id, Constants.KEY_TRANSITION_TYPE),
                Constants.INVALID_INT_VALUE);

        if (lat != Constants.INVALID_FLOAT_VALUE && lng != Constants.INVALID_FLOAT_VALUE &&
                radius != Constants.INVALID_FLOAT_VALUE &&
                expirationDuration != Constants.INVALID_LONG_VALUE &&
                transitionType != Constants.INVALID_INT_VALUE) {

            return new SimpleGeofence(id, lat, lng, radius, expirationDuration, transitionType);
        }
        else {
            return null;
        }
    }

    public void setGeofence(String id, SimpleGeofence geofence) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putFloat(getGeofenceFieldKey(id, Constants.KEY_LATITUDE), (float) geofence.getLatitude());
        editor.putFloat(getGeofenceFieldKey(id, Constants.KEY_LONGITUDE), (float) geofence.getLongitude());
        editor.putFloat(getGeofenceFieldKey(id, Constants.KEY_RADIUS), geofence.getRadius());
        editor.putLong(getGeofenceFieldKey(id, Constants.KEY_EXPIRATION_DURATION), geofence.getExpirationDuration());
        editor.putInt(getGeofenceFieldKey(id, Constants.KEY_TRANSITION_TYPE), geofence.getTransitionType());
        editor.commit();
    }

    public void removeGeofence(String id) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_TRANSITION_TYPE));
        editor.commit();
    }

    private String getGeofenceFieldKey(String id, String fieldName) {
        return Constants.KEY_PREFIX + "_" + id + "_" + fieldName;
    }
}
