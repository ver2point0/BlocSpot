package com.ver2point0.android.blocspot.places;

import android.util.Log;

import com.ver2point0.android.blocspot.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlacesService {

    private String API_KEY;

    public PlacesService(String apiKey) {
        API_KEY = apiKey;
    }

    public void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    public ArrayList<Place> findPlaces(double latitude, double longitude,
                                      String placeSpecification) {

        String urlString = makeUrl(latitude, longitude, placeSpecification);

        try {
            String json = getJSON(urlString);

            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToReferencePoint((JSONObject) array.get(i));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    private String makeUrl(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder(Constants.BASE_URL);

        if (place.equals(Constants.EMPTY_STRING)) {
            urlString.append(Constants.LOCATION);
            urlString.append(Double.toString(latitude));
            urlString.append(Constants.COMMA);
            urlString.append(Double.toString(longitude));
            urlString.append(Constants.ALL_PLACE_TYPES);
            urlString.append(Constants.RANK_BY_DISTANCE);
            urlString.append(Constants.SENSOR_AND_KEY + API_KEY);
        } else {
            urlString.append(Constants.LOCATION);
            urlString.append(Double.toString(latitude));
            urlString.append(Constants.COMMA);
            urlString.append(Double.toString(longitude));
            urlString.append(Constants.KEYWORD + place);
            urlString.append(Constants.ALL_PLACE_TYPES);
            urlString.append(Constants.RANK_BY_DISTANCE);
            urlString.append(Constants.SENSOR_AND_KEY + API_KEY);
        }
        return urlString.toString();
    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

} // end class PlacesService
