package com.ver2point0.android.blocspot.ui.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.places.Place;
import com.ver2point0.android.blocspot.places.PlacesService;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.ArrayList;


public class BlocSpotActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mGoogleMap;
    private String[] mPlaces;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean mListState = true;
    private MapFragment mMapFragment;
    private ListView mPoiList;
    private TextView mEmptyView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.LIST_STATE, mListState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocspot);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBoolean(Constants.LIST_STATE);
        }

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.f_map);
        mPoiList = (ListView) findViewById(R.id.lv_list);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_list_view);
        mPoiList.setEmptyView(mEmptyView);

        initCompo();
        mPlaces = getResources().getStringArray(R.array.places);
        currentLocation();

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
                this, R.array.places, android.R.layout.simple_list_item_1),
                new ActionBar.OnNavigationListener() {
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        Log.e(TAG, mPlaces[itemPosition].toLowerCase().replace("-", "-"));
                        if (mLocation != null) {
                            mGoogleMap.clear();
                            new GetPlaces(BlocSpotActivity.this,
                                    mPlaces[itemPosition].toLowerCase().replace(
                                    "-", "_").replace(" ", "_")).execute();
                        }
                        return true;
                    }
                });

        if (mListState == true) {
            getFragmentManager().beginTransaction().hide(mMapFragment).commit();
        } else if (mListState == false) {
            mPoiList.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {}


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_blocspot);
//        setSupportActionBar(toolbar);
    } // end method onCreate()

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlaces(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        } // end method onPreExecute()

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    getString(R.string.maps_api_key));
            ArrayList<Place> findPlaces = service.findPlaces(mLocation.getLatitude(),
                    mLocation.getLongitude(), places);

            for (int i = 0; i < findPlaces.size(); i++) {
                Place placeDetail = findPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;
        } // end method doInBackground()

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            ArrayList<String> resultName = new ArrayList<String>();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            for (int i = 0; i < result.size(); i++) {
                mGoogleMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName())
                        .position(new LatLng(result.get(i).getLatitude(),
                                result.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin))
                        .snippet(result.get(i).getVicinity()));
                        resultName.add(i, result.get(i).getName());
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                    .get(0).getLongitude()))
                    .zoom(14)
                    .tilt(30)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        } // end method onPostExecute()


    } // end private class GetPlaces

    private void initCompo() {
        mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.f_map)).getMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mListState == true) {
            getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        if (mListState == false) {
            getMenuInflater().inflate(R.menu.menu_map, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
//            if (mListState == true) {
//                getFragmentManager().beginTransaction().show(mMapFragment).commit();
//                mPoiList.setVisibility(View.INVISIBLE);
//                mListState = false;
//            } else {
//                getFragmentManager().beginTransaction().hide(mMapFragment).commit();
//                mPoiList.setVisibility(View.VISIBLE);
//                mListState = true;
//            }
//            this.invalidateOptionsMenu();
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void currentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = mLocationManager.getBestProvider(new Criteria(), true);

        Location location = mLocationManager.getLastKnownLocation(provider);

        if (location == null) {
            mLocationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            mLocation = location;
            new GetPlaces(BlocSpotActivity.this, mPlaces[0].toLowerCase().replace(
                    "-", "_")).execute();
            Log.e(TAG, "location : " + location);
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

} // end class BlocSpotActivity
