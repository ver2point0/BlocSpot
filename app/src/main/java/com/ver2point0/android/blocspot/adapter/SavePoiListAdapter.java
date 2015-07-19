package com.ver2point0.android.blocspot.adapter;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.ArrayList;

public class SavePoiListAdapter extends ArrayAdapter<Category> implements Checkable {

    private ArrayList<Category> mCategories;
    private Context mContext;
    private String mCatName;
    private int mPosition;

    public SavePoiListAdapter(Context context, ArrayList<Category> categories) {
        super(context, R.layout.adapter_save_poi, categories);
        mContext = context;
        mCategories = categories;
    }

    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_poi, null);
            holder = new ViewHolder();
            holder.categoryText = (TextView) convertView.findViewById(R.id.tv_category_text);
            holder.background = (RelativeLayout) convertView.findViewById(R.id.rl_layoutBackground);
            convertView.setTag(holder);
        } else {
           holder = (ViewHolder) convertView.getTag();
        }

        holder.categoryText.setText(mCategories.get(position).getName());
        setColor(position, holder.background);

        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setColor(int position, RelativeLayout background) {
        String color = mCategories.get(position).getColor();

        if (color.equals(Constants.CYAN)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_cyan));
        } else if(color.equals(Constants.BLUE)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_blue));
        } else if(color.equals(Constants.GREEN)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_green));
        } else if(color.equals(Constants.MAGENTA)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_magenta));
        } else if(color.equals(Constants.ORANGE)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_orange));
        } else if(color.equals(Constants.RED)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_red));
        } else if(color.equals(Constants.ROSE)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_rose));
        } else if(color.equals(Constants.VIOLET)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_violet));
        } else if(color.equals(Constants.YELLOW)) {
            background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.clicked_yellow));
        }
    }

    @Override
    public void setChecked(boolean b) {
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }


    private static class ViewHolder {
        TextView categoryText;
        RelativeLayout background;
    }

}
