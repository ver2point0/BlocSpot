package com.ver2point0.android.blocspot.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TinyDataBase {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private File mFolder = null;

    public static String lastImagePath = "";

    public TinyDataBase(Context appContext) {
        mContext = appContext;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public Bitmap getImage(String path) {
        Bitmap retrievedBitmap = null;
        try {
            retrievedBitmap = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retrievedBitmap;
    }

    public String getSavedImagePath() {
        return lastImagePath;
    }

    public String putImagePNG(String theFolder, String theImageName,
                              Bitmap theBitmap) {
        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
        String fullPath = setupFolderPath(theImageName);
        saveBitmapPNG(fullPath, theBitmap);
        lastImagePath = fullPath;
        return fullPath;
    }

    public Boolean putImagePNGWithFullPath(String fullPath, Bitmap theBitmap) {
        return saveBitmapPNG(fullPath, theBitmap);
    }

    private String setupFolderPath(String imageName) {
        File sdCardPath = Environment.getExternalStorageDirectory();
        mFolder = new File(sdCardPath, DEFAULT_APP_IMAGEDATA_DIRECTORY);
        if (!mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("Creating save path",
                        "Default save path creation Error");
            }
        }
        String savePath = mFolder.getPath() + "/" + imageName;
        return savePath;
    }

    private boolean saveBitmapPNG(String strFileName, Bitmap bitmap) {
        if (strFileName == null || bitmap == null)
            return false;
        boolean bSuccess1 = false;
        boolean bSuccess2;
        boolean bSuccess3;
        File saveFile = new File(strFileName);

        if (saveFile.exists()) {
            if (!saveFile.delete())
                return false;
        }

        try {
            bSuccess1 = saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream out = null;

        try {
            out = new FileOutputStream(saveFile);
            bSuccess2 = bitmap.compress(CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
            bSuccess2 = false;
        }
        try {
            if (out != null) {
                out.flush();
                out.close();
                bSuccess3 = true;
            } else {
                bSuccess3 = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            bSuccess3 = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return (bSuccess1 && bSuccess2 && bSuccess3);
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, 0l);
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public double getDouble(String key) {
        String number = getString(key);
        try {
            double value = Double.parseDouble(number);
            return value;
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putDouble(String key, double value) {
        putString(key, String.valueOf(value));
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putList(String key, ArrayList<String> myArray) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String[] mystringlist = myArray.toArray(new String[myArray.size()]);
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // seprating the items in the list
        editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
        editor.apply();
    }

    public ArrayList<String> getList(String key) {
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // separating the items in the list
        String[] myList = TextUtils
                .split(mSharedPreferences.getString(key, ""), "‚‗‚");
        ArrayList<String> retrievedList = new ArrayList<String>(
                Arrays.asList(myList));
        return retrievedList;
    }

    public void putListInt(String key, ArrayList<Integer> myArray) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Integer[] myStringList = myArray.toArray(new Integer[myArray.size()]);
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // separating the items in the list
        editor.putString(key, TextUtils.join("‚‗‚", myStringList));
        editor.apply();
    }

    public ArrayList<Integer> getListInt(String key) {
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // separating the items in the list
        String[] myList = TextUtils
                .split(mSharedPreferences.getString(key, ""), "‚‗‚");
        ArrayList<String> retrievedList1 = new ArrayList<String>(
                Arrays.asList(myList));
        ArrayList<Integer> retrievedList2 = new ArrayList<Integer>();
        for (int i = 0; i < retrievedList1.size(); i++) {
            retrievedList2.add(Integer.parseInt(retrievedList1.get(i)));
        }
        return retrievedList2;
    }

    public void putListBoolean(String key, ArrayList<Boolean> myArray){
        ArrayList<String> origList = new ArrayList<String>();
        for(Boolean b : myArray){
            if (b == true) {
                origList.add("true");
            } else {
                origList.add("false");
            }
        }
        putList(key, origList);
    }

    public ArrayList<Boolean> getListBoolean(String key) {
        ArrayList<String> origList = getList(key);
        ArrayList<Boolean> mBools = new ArrayList<Boolean>();
        for (String b : origList) {
            if (b.equals("true")) {
                mBools.add(true);
            } else {
                mBools.add(false);
            }
        }
        return mBools;
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        return mSharedPreferences.getFloat(key, 0f);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public Boolean deleteImage(String path){
        File toBeDeletedImage = new File(path);
        Boolean isDeleted = toBeDeletedImage.delete();
        return isDeleted;
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

} // end class TinyDataBase
