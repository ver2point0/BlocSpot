package com.ver2point0.android.blocspot.category;


public class Category {

    private String mName;
    private String mColor;

    public Category(String name, String color) {
        mName = name;
        mColor = color;
    }

    public String getName() {
        return mName;
    }

    public String getColor() {
        return mColor;
    }
}

