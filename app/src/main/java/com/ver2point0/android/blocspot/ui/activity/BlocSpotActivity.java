package com.ver2point0.android.blocspot.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.adapter.PoiListAdapter;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.database.table.PoiTable;
import com.ver2point0.android.blocspot.geofence.GeofenceIntentService;
import com.ver2point0.android.blocspot.geofence.SimpleGeofenceStore;
import com.ver2point0.android.blocspot.ui.fragment.ChangeCategoryFragment;
import com.ver2point0.android.blocspot.ui.fragment.EditNoteFragment;
import com.ver2point0.android.blocspot.ui.fragment.FilterDialogFragment;
import com.ver2point0.android.blocspot.ui.fragment.InfoWindowFragment;
import com.ver2point0.android.blocspot.util.Constants;
import com.ver2point0.android.blocspot.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class BlocSpotActivity extends AppCompatActivity
        implements OnMapReadyCallback, FilterDialogFragment.OnFilterListener,
        EditNoteFragment.OnNoteUpdateListener, PoiListAdapter.OnPoiListAdapterListener,
        ChangeCategoryFragment.OnChangeCategoryListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean mListState = true;
    private ListView mPoiList;
    private PoiTable mPoiTable = new PoiTable();
    private SupportMapFragment mMapFragment;
    private String mFilter;
    private InfoWindowFragment mInfoWindowFragment = new InfoWindowFragment();
    private boolean mInProgress;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private ArrayList<Geofence> mCurrentGeofences;
    private ArrayList<String> mGeoIds;
    private SimpleGeofenceStore mGeofenceStore;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.LIST_STATE, mListState);
        outState.putString(Constants.FILTER_TEXT, mFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setContext(this);
        setContentView(R.layout.activity_blocspot);

        Utils.checkIfConnected();

        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBoolean(Constants.LIST_STATE);
            mFilter = savedInstanceState.getString(Constants.FILTER_TEXT);
        }

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.f_map);
        mPoiList = (ListView) findViewById(R.id.lv_list);
        TextView emptyView = (TextView) findViewById(R.id.tv_empty_list_view);
        mPoiList.setEmptyView(emptyView);

        checkCategoryPreference();

        // geofence
        mGoogleApiClient = null;
        mPendingIntent = null;
        mInProgress = false;

        mGeoIds = new ArrayList<String>();
        mGeofenceStore = new SimpleGeofenceStore(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (mListState) {
            getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();
        } else {
            mPoiList.setVisibility(View.INVISIBLE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_blocspot);
        setSupportActionBar(toolbar);
    } // end method onCreate

    @Override
    protected void onStart() {
        super.onStart();
        initComponent();
        currentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilters(mFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mInProgress = false;
                        beginAddGeofences(mGeoIds);
                        break;
                }
        }
    }

    private void beginAddGeofences(ArrayList<String> geoIds) {
        mCurrentGeofences = new ArrayList<Geofence>();

        if (geoIds.size() > 0) {
            for (String id : geoIds) {
                mCurrentGeofences.add(mGeofenceStore.getGeofence(id).toGeofence());
            }
        }

        if (!servicesConnected()) {
            return;
        }

        if (!mInProgress) {
            mInProgress = true;
            mGoogleApiClient.connect();
        } else {
            mInProgress = false;
            beginAddGeofences(geoIds);
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), Constants.APPTAG);
            }
            return false;
        }
    }

    private PendingIntent getTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            Log.d("TEST LOCATION", mLocation.getLatitude() + "");
            Log.d("TEST LOCATION", mLocation.getLongitude() + "");
        }
//        mPendingIntent = getTransitionPendingIntent();
//        LocationServices.GeofencingApi
//                .addGeofences(mGoogleApiClient, mCurrentGeofences, mPendingIntent)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (!status.isSuccess()) {
//                            Toast.makeText(BlocSpotActivity.this,
//                                    getString(R.string.toast_geofences_failed), Toast.LENGTH_SHORT).show();
//                        }
//                        mInProgress = false;
//                        mGoogleApiClient.disconnect();
//                    }
//                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        mInProgress = false;
        mGoogleApiClient = null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mInProgress = false;
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException ignored) {
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode, this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(), "Geofence Detection");
            }
        }
    }


    private void checkCategoryPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MAIN_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>() {
        }.getType();
        ArrayList<Category> categories = new Gson().fromJson(json, type);
        if (categories == null) {
            categories = new ArrayList<Category>();
            Category uncategorized = new Category(Constants.CATEGORY_UNCATEGORIZED, Constants.CYAN);
            categories.add(uncategorized);
            String jsonCat = new Gson().toJson(categories);
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.putString(Constants.CATEGORY_ARRAY, jsonCat);
            prefsEditor.apply();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    @Override
    public void applyFilters(String name) {
        mFilter = name;
        currentLocation();
    }

    @Override
    public void updateNoteDataBase(final String id, final String note) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.updateNote(id, note);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_poi_updated),
                                Toast.LENGTH_LONG).show();
                        refreshList(id);
                    }
                });
            }
        }.start();
    }

    @Override
    public void editNoteDialog(String id, String note) {
        EditNoteFragment dialog = new EditNoteFragment(id, this, note);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void editVisited(final String id, final Boolean visited) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.updateVisited(id, visited);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_poi_updated),
                                Toast.LENGTH_LONG).show();
                        refreshList(id);
                    }
                });
            }
        }.start();
    }

    @Override
    public void viewOnMap(String lat, String lng) {
        getSupportFragmentManager().beginTransaction().show(mMapFragment).commit();
        mPoiList.setVisibility(View.INVISIBLE);
        mListState = false;
        this.invalidateOptionsMenu();

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(20)
                .tilt(0)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void deletePoi(final String id, final String geoId) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.deletePoi(id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_delete_poi),
                                Toast.LENGTH_LONG).show();
                        mGeofenceStore.removeGeofence(geoId);
                        refreshList(id);
                    }
                });
            }
        }.start();
    }

    @Override
    public void changeCategory(String id) {
        ChangeCategoryFragment dialog = new ChangeCategoryFragment(id, this);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void shareLocation(String name, String lat, String lng) {
        String newName = name.replace(" ", "+");
        String shareUrl = "https://www.google.com/maps/place" + newName + "/@" + lat + "," + lng;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(Constants.INTENT_TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_SUBJECT, name);
        intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(intent, getString(R.string.intent_share_poi)));
    }

    @Override
    public void refreshList(String id) {
        currentLocation();
        if (mInfoWindowFragment != null) {
            mInfoWindowFragment.refreshInfoWindow(id);
        }
    }

    private class GetPlaces extends AsyncTask<Void, Void, Cursor> {

        private ProgressDialog dialog;
        private Context context;
        private String filter;

        public GetPlaces(Context context, String filter) {
            this.context = context;
            this.filter = filter;
            mGeoIds.clear();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.loading_message));
                dialog.isIndeterminate();
                dialog.show();
            } catch (Exception ignored) {

            }
        } // end method onPreExecute()

        @Override
        protected Cursor doInBackground(Void... arg0) {
            Cursor cursor = null;
            try {
                if (filter != null) {
                    cursor = mPoiTable.filterQuery(filter);
                } else {
                    cursor = mPoiTable.poiQuery();
                }
            } catch (Exception ignored) {

            }
            return cursor;
        } // end method doInBackground()

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if (dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                    ignored.printStackTrace();
                }
            }

            PoiListAdapter adapter = new PoiListAdapter(BlocSpotActivity.this, cursor, mLocation);
            mPoiList.setAdapter(adapter);

            Cursor c;
            mGoogleMap.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                c = ((Cursor) adapter.getItem(i));
                mGoogleMap.addMarker(new MarkerOptions()
                        .title(c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_ID)))
                        .snippet(c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_GEO_ID)))
                        .position(new LatLng(c.getDouble(c.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE)),
                                c.getDouble(c.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE))))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(getMarkerColor(c))));
                mGeoIds.add(c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_GEO_ID)));
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                    .zoom(14)
                    .tilt(0)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            beginAddGeofences(mGeoIds);
        } // end method onPostExecute()

        private float getMarkerColor(Cursor c) {
            float colorId = 0;
            String color = c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
            switch (color) {
                case Constants.CYAN:
                    colorId = BitmapDescriptorFactory.HUE_CYAN;
                    break;
                case Constants.BLUE:
                    colorId = BitmapDescriptorFactory.HUE_BLUE;
                    break;
                case Constants.GREEN:
                    colorId = BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case Constants.MAGENTA:
                    colorId = BitmapDescriptorFactory.HUE_MAGENTA;
                    break;
                case Constants.ORANGE:
                    colorId = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case Constants.RED:
                    colorId = BitmapDescriptorFactory.HUE_RED;
                    break;
                case Constants.ROSE:
                    colorId = BitmapDescriptorFactory.HUE_ROSE;
                    break;
                case Constants.VIOLET:
                    colorId = BitmapDescriptorFactory.HUE_VIOLET;
                    break;
                case Constants.YELLOW:
                    colorId = BitmapDescriptorFactory.HUE_YELLOW;
                    break;
            }
            return colorId;
        }
    } // end private class GetPlaces

    private void initComponent() {
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {
                        InfoWindowFragment fragment = new InfoWindowFragment(marker.getTitle(),
                                marker.getSnippet(), BlocSpotActivity.this);
                        fragment.show(getFragmentManager(), "dialog");
                        return true;
                    }
                });
            }
        });
    }

    public static final String SEARCH_QUERY = "com.ver2point0.android.blocspot.ui.BlocSpotActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mListState) {
            getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        if (!mListState) {
            getMenuInflater().inflate(R.menu.menu_map, menu);
        }
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        final Intent searchResultsIntent = new Intent(this, SearchResultsActivity.class);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("ON QUERY", query);
                searchResultsIntent.putExtra(SEARCH_QUERY, query);
                startActivity(searchResultsIntent);
                /*
                * create intent to searchresults activity
                * pass queryString to searchresults activity using Extra
                * startactivity(intent)
                * */
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_switch) {
            if (mListState) {
                getSupportFragmentManager().beginTransaction().show(mMapFragment).commit();
                mPoiList.setVisibility(View.INVISIBLE);
                mListState = false;
            } else {
                getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();
                mPoiList.setVisibility(View.VISIBLE);
                mListState = true;
            }
            this.invalidateOptionsMenu();
//        } else if (id == R.id.action_search) {
//            Intent intent = new Intent(this, SearchResultsActivity.class);
//            startActivity(intent);
        } else if (id == R.id.action_filter) {
            FilterDialogFragment dialog = new FilterDialogFragment(this);
            dialog.show(getFragmentManager(), "dialog");
        }
        return super.onOptionsItemSelected(item);
    }

    private void currentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        String provider = mLocationManager.getBestProvider(new Criteria(), true);

        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            Toast.makeText(this, getString(R.string.toast_no_gps), Toast.LENGTH_SHORT).show();
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        } else {
            mLocation = location;
            new GetPlaces(BlocSpotActivity.this, mFilter).execute();
        }
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            mLocation = location;
            mLocationManager.removeUpdates(listener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


} // end class BlocSpotActivity
