package com.ver2point0.android.blocspot;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ver2point0.android.blocspot.database.BlocSpotDataBaseHelper;


public class BlocSpotApplication extends Application {

    private static BlocSpotDataBaseHelper mDatabase;
    private static Context mContext;

    public BlocSpotApplication() {}

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new BlocSpotDataBaseHelper(getApplicationContext());
        BlocSpotApplication.mContext = getApplicationContext();
    }

    public SQLiteDatabase getWritableDb() {
        return mDatabase.getWritableDatabase();
    }

    public static BlocSpotApplication get() {
        if (BlocSpotApplication.mContext == null) {

        }
        return (BlocSpotApplication) BlocSpotApplication.mContext;
    }
}
