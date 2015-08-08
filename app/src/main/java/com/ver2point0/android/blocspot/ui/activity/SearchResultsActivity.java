package com.ver2point0.android.blocspot.ui.activity;

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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.adapter.PlacesSearchItemAdapter;
import com.ver2point0.android.blocspot.api.Yelp;
import com.ver2point0.android.blocspot.places.Place;
import com.ver2point0.android.blocspot.places.PlacesService;
import com.ver2point0.android.blocspot.ui.fragment.SavePoiDialogFragment;
import com.ver2point0.android.blocspot.util.Constants;
import com.ver2point0.android.blocspot.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResultsActivity extends FragmentActivity implements SavePoiDialogFragment.OnSavePoiInteractionListener {

    private final String TAG = getClass().getSimpleName();

    private LocationManager mLocationManager;
    private Location mLocation;
    private ListView mSearchList;
    private String mQuery;
    PlacesSearchItemAdapter mAdapter;
    private ArrayList<Business> mBusinessArrayList;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.QUERY_TEXT, mQuery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setContext(this);
        setContentView(R.layout.activity_search);

        mSearchList = (ListView) findViewById(R.id.lv_searchList);

        mQuery = getIntent().getStringExtra(BlocSpotActivity.SEARCH_QUERY);
        new AsyncTask<Void, Void, ArrayList<Business>>() {
            @Override
            protected ArrayList<Business> doInBackground(Void... params) {
                String jsonBusinesses = Yelp
                        .getYelp(SearchResultsActivity.this)
                        .search(mQuery, "Anchorage, AK");
                try {
                    return processJson(jsonBusinesses);
                } catch (JSONException e) {
                    return (ArrayList) Collections.<Business>emptyList();
                }
            }

            @Override
            protected void onPostExecute(ArrayList<Business> businessList) {
                ArrayList<String> businessNameList = new ArrayList<String>();
                for (Business b : businessList) {
                    Log.i("SearchResults", b.name);
                    businessNameList.add(b.name);
                }
                mBusinessArrayList = businessList;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResultsActivity.this, android.R.layout.simple_list_item_1, businessNameList);
                mSearchList.setAdapter(adapter);
            }
        }.execute();

        Utils.checkIfConnected();
        if (savedInstanceState != null) {
            //mQuery = savedInstanceState.getString(Constants.QUERY_TEXT);

        }



        if (Utils.haveNetworkConnection()) {
            if (mQuery != null) {
                currentLocation(mQuery.toLowerCase().replace("-", "_").replace(" ", "_"));
            } else {
                currentLocation(Constants.EMPTY_STRING);
            }
        }

        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Place place = (Place) adapterView.getItemAtPosition(position);
                SavePoiDialogFragment savePoiDialogFragment = new SavePoiDialogFragment(SearchResultsActivity.this, place);
                savePoiDialogFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    ArrayList<Business> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<Business> businessObjs = new ArrayList<Business>(businesses.length());
        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            businessObjs.add(new Business(business.optString("name"), business.optString("mobile_url")));
        }
        return businessObjs;
    }

    class Business {
        final String name;
        final String url;

        public Business(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public String toString() {
            return name;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setContext(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(true);
//        searchView.setFocusable(true);
//        searchView.setSubmitButtonEnabled(true);
//        searchView.requestFocusFromTouch();

        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("ON QUERY", query);
//                1) User enters search
//                2) Put list results into some List type data structure
//                3) Update the ListView adapter with the new data structure
//                4) Set the newly updated adapter to the ListView

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
        if (id == R.id.item_search) {
            Toast.makeText(this, "Search pressed", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void currentLocation(String query) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(new Criteria(), true);

        Location location = mLocationManager.getLastKnownLocation(provider);

        if (location == null) {
            Toast.makeText(this, getString(R.string.toast_no_gps), Toast.LENGTH_SHORT).show();
            mLocationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            mLocation = location;
            new GetPlaces(SearchResultsActivity.this, query).execute();
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

    @Override
    public void returnToMain() {
        Intent intent = new Intent(this, BlocSpotActivity.class);
        startActivity(intent);
    }

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
            PlacesService service = new PlacesService(Constants.API_KEY);
            ArrayList<Place> findPlaces = service.findPlaces(mLocation.getLatitude(), mLocation.getLongitude(), searchText);
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

} // end class SearchResultsActivity
