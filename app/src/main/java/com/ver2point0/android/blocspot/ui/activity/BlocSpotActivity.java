package com.ver2point0.android.blocspot.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.ver2point0.android.blocspot.ui.fragment.ChangeCategoryFragment;
import com.ver2point0.android.blocspot.ui.fragment.EditNoteFragment;
import com.ver2point0.android.blocspot.ui.fragment.FilterDialogFragment;
import com.ver2point0.android.blocspot.ui.fragment.InfoWindowFragment;
import com.ver2point0.android.blocspot.util.Constants;
import com.ver2point0.android.blocspot.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class BlocSpotActivity extends FragmentActivity
        implements OnMapReadyCallback, FilterDialogFragment.OnFilterListener,
        EditNoteFragment.OnNoteUpdateListener, PoiListAdapter.OnPoiListAdapterListener,
        ChangeCategoryFragment.OnChangeCategoryListener {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean mListState = true;
    private ListView mPoiList;
    private PoiTable mPoiTable = new PoiTable();
    private MapFragment mMapFragment;
    private String mFilter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.LIST_STATE, mListState);
        outState.putString(Constants.FILTER_TEXT, mFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocspot);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBoolean(Constants.LIST_STATE);
            mFilter = savedInstanceState.getString(Constants.FILTER_TEXT);
        }

        Utils.setContext(this);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.f_map);
        mPoiList = (ListView) findViewById(R.id.lv_list);
        TextView emptyView = (TextView) findViewById(R.id.tv_empty_list_view);
        mPoiList.setEmptyView(emptyView);

        checkCategoryPreference();

        initCompo();
        currentLocation();

        if (mListState) {
            getFragmentManager().beginTransaction().hide(mMapFragment).commit();
        } else {
            mPoiList.setVisibility(View.INVISIBLE);
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_blocspot);
//        setSupportActionBar(toolbar);
    } // end method onCreate

    @Override
    protected void onResume() {
        super.onResume();
        applyFilters(mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setContext(null);
    }

    private void checkCategoryPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPreferences.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<Category>(){}.getType();
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
    public void onMapReady(GoogleMap googleMap) {}

    @Override
    public void applyFilters(String name) {
        mFilter = name;
        new GetPlaces(BlocSpotActivity.this, name).execute();
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
                        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
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
                Log.e("ERROR", String.valueOf(visited));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_poi_updated),
                                Toast.LENGTH_LONG).show();
                        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
                    }
                });
            }
        }.start();
    }

    @Override
    public void viewOnMap(String lat, String lng) {
        getFragmentManager().beginTransaction().show(mMapFragment).commit();
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
    public void deletePoi(final String id) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.deletePoi(id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, "POI Deleted!",
                                Toast.LENGTH_LONG).show();
                        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
                    }
                });
            }
        }.start();
    }

    @Override
    public void changeCategory(String id){
        ChangeCategoryFragment dialog = new ChangeCategoryFragment(id, this);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void shareLocation(String name, String lat, String lng) {
        name = name.replace(" ", "+");
        String shareUrl = "https://www.google.com/maps/place" + name + "/@" + lat + "," + lng;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(Constants.INTENT_TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_SUBJECT, name);
        intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(intent, getString(R.string.intent_share_poi)));
    }



    @Override
    public void refreshList() {
        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
    }

    private class GetPlaces extends AsyncTask<Void, Void, Cursor> {

        private ProgressDialog dialog;
        private Context context;
        private Exception ex;
        private String filter;

        public GetPlaces(Context context, String filter) {
            this.context = context;
            this.filter = filter;
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
            } catch (Exception e){
//                dialog.dismiss();
                Log.e("ERROR_PRE", String.valueOf(e));
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
            } catch (Exception e) {
                ex = e;
                Log.e("ERROR_DO", String.valueOf(ex));
            }
            return cursor;
        } // end method doInBackground()

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if (ex != null) {
                Log.e("ERROR_POST", String.valueOf(ex));
                dialog.dismiss();
            }

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
                        .title(cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_ID)))
                        .position(new LatLng(cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE)),
                                cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE))))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(getMarkerColor(c))));
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                    .zoom(14)
                    .tilt(0)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        } // end method onPostExecute()

        private float getMarkerColor(Cursor c) {
            float colorId = 0;
            String color = c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
            if(color.equals(Constants.CYAN)) {
                colorId = BitmapDescriptorFactory.HUE_CYAN;
            } else if(color.equals(Constants.BLUE)) {
                colorId = BitmapDescriptorFactory.HUE_BLUE;
            } else if(color.equals(Constants.GREEN)) {
                colorId = BitmapDescriptorFactory.HUE_GREEN;
            } else if(color.equals(Constants.MAGENTA)) {
                colorId = BitmapDescriptorFactory.HUE_MAGENTA;
            } else if(color.equals(Constants.ORANGE)) {
                colorId = BitmapDescriptorFactory.HUE_ORANGE;
            } else if(color.equals(Constants.RED)) {
                colorId = BitmapDescriptorFactory.HUE_RED;
            } else if(color.equals(Constants.ROSE)) {
                colorId = BitmapDescriptorFactory.HUE_ROSE;
            } else if(color.equals(Constants.VIOLET)) {
                colorId = BitmapDescriptorFactory.HUE_VIOLET;
            } else if(color.equals(Constants.YELLOW)) {
                colorId = BitmapDescriptorFactory.HUE_YELLOW;
            }
            return colorId;
        }
    } // end private class GetPlaces

    private void initCompo() {
        mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.f_map)).getMap();
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                InfoWindowFragment fragment = new InfoWindowFragment(marker.getTitle(), BlocSpotActivity.this);
                fragment.show(getFragmentManager(), "dialog");
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mListState) {
            getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        if (!mListState) {
            getMenuInflater().inflate(R.menu.menu_map, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_switch) {
            if (mListState) {
                getFragmentManager().beginTransaction().show(mMapFragment).commit();
                mPoiList.setVisibility(View.INVISIBLE);
                mListState = false;
            } else {
                getFragmentManager().beginTransaction().hide(mMapFragment).commit();
                mPoiList.setVisibility(View.VISIBLE);
                mListState = true;
            }
            this.invalidateOptionsMenu();
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_filter) {
            FilterDialogFragment dialog = new FilterDialogFragment(this);
            dialog.show(getFragmentManager(), "dialog");
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
            new GetPlaces(BlocSpotActivity.this, null).execute();
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
