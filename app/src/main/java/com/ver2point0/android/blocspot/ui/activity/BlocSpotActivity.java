package com.ver2point0.android.blocspot.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.places.Place;
import com.ver2point0.android.blocspot.places.PlacesService;

import java.util.ArrayList;


public class BlocSpotActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mGoogleMap;
    private String[] mPlaces;
    private LocationManager mLocationManager;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocspot);

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
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            for (int i = 0; i < result.size(); i++) {
                mGoogleMap.addMarker(new MarkerOptions()
                .title(result.get(i).getName()
                .position(new LatLng(result.get(i).getLatitude(),
                        result.get(i).getLongitude()))
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.pin))
                .snippet(result.get(i).getVicinity())));
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

    } // end private class GetPlaces


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this add items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_blocspot, menu);
        return true;
    }



//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case R.id.action_one:
//                break;
//            case R.id.action_two:
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        return false;
//    }



} // end class BlocSpotActivity
