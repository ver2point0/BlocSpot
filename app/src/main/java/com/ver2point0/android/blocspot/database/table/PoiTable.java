package com.ver2point0.android.blocspot.database.table;


import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.ver2point0.android.blocspot.util.Constants;

public class PoiTable extends Table {

    private static final String SQL_CREATE_POI =
            "CREATE TABLE " + Constants.TABLE_POI_NAME + " (" +
                    Constants.TABLE_COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Constants.TABLE_COLUMN_POI_NAME + " TEXT," +
                    Constants.TABLE_COLUMN_NOTE + " TEXT," +
                    Constants.TABLE_COLUMN_VISITED + " TEXT," +
                    Constants.TABLE_COLUMN_LATITUDE + " DOUBLE," +
                    Constants.TABLE_COLUMN_LONGITUDE + " DOUBLE," +
                    Constants.TABLE_COLUMN_CAT_NAME + " TEXT," +
                    Constants.TABLE_COLUMN_CAT_COLOR + " TEXT," +
                    Constants.TABLE_COLUMN_GEO_ID + " TEXT," +
                    "UNIQUE(" + Constants.TABLE_COLUMN_POI_NAME +
                    ") ON CONFLICT REPLACE"+
                    " )";
    
    public PoiTable() {
        super(Constants.TABLE_POI_NAME);
    }

    @Override
    public String getCreateStatement() {
        return SQL_CREATE_POI;
    }

    public void addNewPoi(String name, double lat, double lng, String catName, String catColor, String geoId) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_POI_NAME, name);
        values.put(Constants.TABLE_COLUMN_LATITUDE, lat);
        values.put(Constants.TABLE_COLUMN_LONGITUDE, lng);
        values.put(Constants.TABLE_COLUMN_CAT_NAME, catName);
        values.put(Constants.TABLE_COLUMN_CAT_COLOR, catColor);
        values.put(Constants.TABLE_COLUMN_NOTE, "");
        values.put(Constants.TABLE_COLUMN_VISITED, false);
        values.put(Constants.TABLE_COLUMN_GEO_ID, geoId);
        mDatabase.insert(Constants.TABLE_POI_NAME, null, values);
    }

    public Cursor poiQuery() {
        return mDatabase.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                Constants.TABLE_COLUMN_NOTE, Constants.TABLE_COLUMN_VISITED,
                Constants.TABLE_COLUMN_LATITUDE, Constants.TABLE_COLUMN_LONGITUDE,
                Constants.TABLE_COLUMN_CAT_NAME, Constants.TABLE_COLUMN_CAT_COLOR,
                Constants.TABLE_COLUMN_GEO_ID},
                null, null, null, null, null, null);
    }

    public Cursor poiSpecificQuery(String id) {
        Log.e("ERRORID", id);
        return mDatabase.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                Constants.TABLE_COLUMN_NOTE, Constants.TABLE_COLUMN_VISITED,
                Constants.TABLE_COLUMN_LATITUDE, Constants.TABLE_COLUMN_LONGITUDE,
                Constants.TABLE_COLUMN_CAT_NAME, Constants.TABLE_COLUMN_CAT_COLOR},
                Constants.TABLE_COLUMN_ID + " = ?",
                new String[]{id},
                null, null, null, null);
    }



    public Cursor filterQuery(String filter) {
        return mDatabase.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                        Constants.TABLE_COLUMN_NOTE, Constants.TABLE_COLUMN_VISITED,
                        Constants.TABLE_COLUMN_LATITUDE, Constants.TABLE_COLUMN_LONGITUDE,
                        Constants.TABLE_COLUMN_CAT_NAME, Constants.TABLE_COLUMN_CAT_COLOR},
                Constants.TABLE_COLUMN_CAT_NAME + " = ?",
                new String[]{filter},
                null, null, null, null);
    }

    public Cursor poiCheck(String name) {
        return mDatabase.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                Constants.TABLE_COLUMN_CAT_COLOR},
                Constants.TABLE_COLUMN_POI_NAME + " = ?",
                new String[]{name},
                null, null, null, null);
    }

    public void updateNote(String id, String note) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_NOTE, note);
        mDatabase.update(Constants.TABLE_POI_NAME, values,
                Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }

    public void updateVisited(String id, boolean visited) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_VISITED, visited);
        mDatabase.update(Constants.TABLE_POI_NAME, values,
                Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }

    public void deletePoi(String id) {
        mDatabase.delete(Constants.TABLE_POI_NAME, Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }

    public void updateCategory(String id, String category, String catColor) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_CAT_NAME, category);
        values.put(Constants.TABLE_COLUMN_CAT_COLOR, catColor);
        mDatabase.update(Constants.TABLE_POI_NAME, values,
                Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }
}
