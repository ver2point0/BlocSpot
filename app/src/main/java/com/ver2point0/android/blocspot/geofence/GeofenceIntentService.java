package com.ver2point0.android.blocspot.geofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.ver2point0.android.blocspot.BlocSpotApplication;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.database.table.PoiTable;
import com.ver2point0.android.blocspot.ui.activity.BlocSpotActivity;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.List;


public class GeofenceIntentService extends IntentService {

    private PoiTable mPoiTable = new PoiTable();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public GeofenceIntentService() {
        super("GeofenceIntentService");
        mSharedPreferences = BlocSpotApplication.get().getSharedPreferences(Constants.NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            int transition = geofencingEvent.getGeofenceTransition();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
                String[] geofenceIds = new String[geofenceList.size()];

                for (int i = 0; i < geofenceIds.length; i++) {
                    geofenceIds[i] = geofenceList.get(i).getRequestId();
                }

                String queryString = makePlaceHolders(geofenceIds.length);
                new GetPlaceName(queryString, geofenceIds).execute();
            }
        }
    }

    String makePlaceHolders(int length) {
        if (length < 1) {
            return Constants.EMPTY_STRING;
        } else {
            StringBuilder sb = new StringBuilder(length * 2 - 1);
            sb.append("?");
            for (int i = 1; i < length; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    private void sendNotification(String geoName, int i) {
        if (System.currentTimeMillis() - mSharedPreferences.getLong(geoName, 0) > Constants.TWENTY_MINUTES
                || mSharedPreferences.getLong(geoName, 0) == 0) {
            mEditor.putLong(geoName, System.currentTimeMillis());
            mEditor.commit();

            Intent notificationIntent = new Intent(getApplicationContext(), BlocSpotActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(BlocSpotActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent notificationPendingIntent = stackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(geoName)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText(getString(R.string.notification_poi))
                    .setContentIntent(notificationPendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(i, builder.build());
        }
    }

    private class GetPlaceName extends AsyncTask<Void, Void, Cursor> {

        private String queryString;
        private String[] geofenceIds;

        public GetPlaceName(String queryString, String[] geofenceIds) {
            this.queryString = queryString;
            this.geofenceIds = geofenceIds;
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return mPoiTable.notificatinoQuery(queryString, geofenceIds);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            String geoName;
            int i = 0;

            while (cursor.moveToNext()) {
                geoName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
                sendNotification(geoName, i);
                i++;
            }
        }
    }
}
