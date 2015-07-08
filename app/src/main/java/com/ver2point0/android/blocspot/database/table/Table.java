package com.ver2point0.android.blocspot.database.table;


import android.database.sqlite.SQLiteDatabase;

import com.ver2point0.android.blocspot.BlocSpotApplication;

public abstract class Table {

    protected boolean mLoaded = false;
    private String TABLE_NAME;
    SQLiteDatabase mDatabase;

    public Table(String name) {
        TABLE_NAME = name;
        load();
    }

    public String getName() {
        return TABLE_NAME;
    }

    public final void load() {
        if (isLoaded()) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                mDatabase = BlocSpotApplication.get().getWritableDb();
            }
        }.start();
        setLoaded(true);
    }

    protected  void setLoaded(boolean loaded) {
        mLoaded = loaded;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public abstract String getCreateStatement();

    public abstract void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
}
