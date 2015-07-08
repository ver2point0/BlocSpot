package com.ver2point0.android.blocspot.database.table;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ver2point0.android.blocspot.util.Constants;

public class PoiTable extends Table {

    private static final String SQL_CREATE_POI =
            "CREATE TABLE " + Constants.TABLE_POI_NAME + " (" +
                    Constants.TABLE_COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Constants.TABLE_COLUMN_POI_NAME + " Text," +
                    Constants.TABLE_COLUMN_LATITUDE + " DOUBLE," +
                    Constants.TABLE_COLUMN_LONGITUDE + " DOUBLE," +
                    Constants.TABLE_COLUMN_CAT_NAME + " TEXT," +
                    Constants.TABLE_COLUMN_CAT_COLOR + " TEXT," +
                    " )";
    
    public PoiTable() {
        super(Constants.TABLE_POI_NAME);
    }

    @Override
    public String getCreateStatement() {
        return SQL_CREATE_POI;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

    public void addNewPoi(String name, double lat, double lng, String catName, String catColor) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_POI_NAME, name);
        values.put(Constants.TABLE_COLUMN_LATITUDE, lat);
        values.put(Constants.TABLE_COLUMN_LONGITUDE, lng);
        values.put(Constants.TABLE_COLUMN_CAT_NAME, catName);
        values.put(Constants.TABLE_COLUMN_CAT_COLOR, catColor);
        mDatabase.insert(Constants.TABLE_POI_NAME, null, values);
    }

    public Cursor notesQuery() {
        return mDatabase.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME},
                null, null, null, null, null, null);
    }
}
