package com.ver2point0.android.blocspot.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ver2point0.android.blocspot.database.table.PoiTable;
import com.ver2point0.android.blocspot.database.table.Table;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BlocSpotDataBaseHelper extends SQLiteOpenHelper {

    private static Set<Table> sTables = new HashSet<Table>();
    static {
        sTables.add(new PoiTable());
    }

    public BlocSpotDataBaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Iterator<Table> tables = sTables.iterator();
        while (tables.hasNext()) {
            sqLiteDatabase.execSQL(tables.next().getCreateStatement());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {}
}
