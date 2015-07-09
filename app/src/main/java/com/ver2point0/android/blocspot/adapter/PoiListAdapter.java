package com.ver2point0.android.blocspot.adapter;


import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.util.Constants;
import com.ver2point0.android.blocspot.util.Utils;

public class PoiListAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private View mView;
    private final LayoutInflater mInflater;
    private Location mLocation;
    private String mName;


    public PoiListAdapter(Context context, Cursor cursor, Location location) {
        super(context, cursor);
        mContext = context;
        mCursor = cursor;
        mInflater = LayoutInflater.from(context);
        mLocation = location;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        mView = mInflater.inflate(R.layout.adapter_poi_list, null);

        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) mView.findViewById(R.id.tv_place_name);
        holder.note = (TextView) mView.findViewById(R.id.tv_note_text);
        holder.checkMark = (ImageView) mView.findViewById(R.id.iv_check_image);
        holder.dist = (TextView) mView.findViewById(R.id.tv_place_dist);
        holder.threeDots = (ImageButton) mView.findViewById(R.id.ib_three_dots);
        holder.color = (TextView) mView.findViewById(R.id.tv_color_area);
        mView.setTag(holder);
        return mView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        mName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
        String note = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_NOTE));
        Boolean visited = cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COLUMN_VISITED)) > 0;
        Double lat = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE));
        Double lng = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE));
        String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));

        holder.name.setText(mName);
        if(note != null) {
            holder.note.setText(note);
        }

        Location placeLoc = new Location("");
        placeLoc.setLatitude(lat);
        placeLoc.setLongitude(lng);
        float dist = (float) (mLocation.distanceTo(placeLoc) / 1609.34);
        holder.dist.setText(String.format("%.2f", dist) + " mi");

        if(visited != null && visited == true) {
            holder.checkMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_on));
        }

        Utils.setColorString(color, holder.color);
    }

    public String getName() {
        return mName;
    }
    private static class ViewHolder {
        TextView name;
        TextView note;
        TextView dist;
        TextView color;
        ImageView checkMark;
        ImageButton threeDots;
    }
}
