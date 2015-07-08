package com.ver2point0.android.blocspot.places;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Place {
    private String mId;
    private String mIcon;
    private String mName;
    private String mVicinity;
    private Double mLatitude;
    private Double mLongitude;
    private JSONArray mTypes;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getVicinity() {
        return mVicinity;
    }

    public void setVicinity(String vicinity) {
        mVicinity = vicinity;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public JSONArray getTypes() {
        return mTypes;
    }

    public void setTypes(JSONArray types) {
        mTypes = types;
    }

    static Place jsonToReferencePoint(JSONObject referencePoint) {
        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) referencePoint.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(referencePoint.getString("icon"));
            result.setName(referencePoint.getString("name"));
            result.setVicinity(referencePoint.getString("vicinity"));
            result.setId(referencePoint.getString("id"));
            result.setTypes(referencePoint.getJSONArray("types"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + mId + ", icon=" + mIcon + ", name=" + mName
                + ", latitude=" + mLatitude + ", longitude=" + mLongitude + "}";
    }

} // end class Place
