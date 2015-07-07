package com.ver2point0.android.blocspot.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.places.Place;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PlacesSearchItemAdapter extends ArrayAdapter<Place> {

    private Context mContext;
    private ArrayList<Place> mPlaceList;

    public PlacesSearchItemAdapter(Context context, ArrayList<Place> places) {
        super(context, R.layout.adapter_places_search_item, places);
        mContext = context;
        mPlaceList = places;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_places_search_item, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.tv_place_name);
            holder.typeLabel = (TextView) convertView.findViewById(R.id.tv_place_type);
            holder.distanceLabel = (TextView) convertView.findViewById(R.id.tv_place_dist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONArray placeType = mPlaceList.get(position).getTypes();
        String type = null;
        try {
            type = placeType.getString(0).replace(" ", " ").replace("_", " ");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String typeCap = capitalizeString(type);

        holder.nameLabel.setText(mPlaceList.get(position).getName());
        holder.typeLabel.setText(typeCap);

        return convertView;
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    private static class ViewHolder {
        TextView nameLabel;
        TextView typeLabel;
        TextView distanceLabel;
    }
}
