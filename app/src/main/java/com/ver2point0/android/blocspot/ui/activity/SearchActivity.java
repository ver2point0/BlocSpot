package com.ver2point0.android.blocspot.ui.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.adapter.PlacesSearchItemAdapter;
import com.ver2point0.android.blocspot.places.Place;
import com.ver2point0.android.blocspot.places.PlacesService;
import com.ver2point0.android.blocspot.ui.fragment.SavePoiDialogFragment;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.ArrayList;

public class SearchActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();

    private LocationManager mLocationManager;
    private Location mLocation;
    private String[] mPlaces;
    private ListView mSearchList;
    private String mQuery;
    PlacesSearchItemAdapter mAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.QUERY_TEXT, mQuery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(Constants.QUERY_TEXT);
        }

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            mQuery = getIntent().getStringExtra(SearchManager.QUERY);
        }

        mPlaces = getResources().getStringArray(R.array.places);
        currentLocation();
        mSearchList = (ListView) findViewById(R.id.lv_searchList);

        if (mQuery != null) {
            new GetPlaces(SearchActivity.this,
                    mQuery.toLowerCase().replace("-", "_").replace(" ", "_")).execute();
        }

        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Place place = (Place) adapterView.getItemAtPosition(position);
                SavePoiDialogFragment savePoiDialogFragment = new SavePoiDialogFragment(SearchActivity.this, place);
                savePoiDialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.lv_searchList).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.requestFocusFromTouch();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
            new GetPlaces(SearchActivity.this, "").execute();
            Log.e(TAG, "location : " + location);
        }
    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            mLocation = location;
            mLocationManager.removeUpdates(listener);
        }
    };

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String searchText;

        public GetPlaces(Context context, String searchText) {
            this.context = context;
            this.searchText = searchText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.loading_message));
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    Constants.API_KEY);
            ArrayList<Place> findPlaces = service.findPlaces(mLocation.getLatitude(),
                    mLocation.getLongitude(), searchText);
            return findPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);

            ArrayList<String> resultName = new ArrayList<String>();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            for (int i = 0; i < result.size(); i++) {
                resultName.add(i, result.get(i).getName());
            }

            mAdapter = new PlacesSearchItemAdapter(context, result, mLocation);
            mSearchList.setTextFilterEnabled(true);
            mSearchList.setAdapter(mAdapter);
        }

    } // end method GetPlaces()

} // end class SearchActivity