package com.ver2point0.android.blocspot.adapter;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
    private ArrayList<Boolean> mItemChecked;
    private ViewHolder mViewHolder;

    public SavePoiListAdapter(Context context, ArrayList<Category> categories) {
        super(context, R.layout.adapter_save_poi, categories);
        mContext = context;
        mCategories = categories;
        mItemChecked = new ArrayList<Boolean>();
    }

    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_poi, null);
            mViewHolder = new ViewHolder();
            mViewHolder.categoryText = (TextView) convertView.findViewById(R.id.tv_category_text);
            mViewHolder.background = (RelativeLayout) convertView.findViewById(R.id.layoutBackground);
            convertView.setTag(mViewHolder);
        } else {
           mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.categoryText.setText(mCategories.get(position).getName());
        setColor(position, mCategories, mViewHolder.background);

        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setColor(int position, ArrayList<Category> categories, RelativeLayout background) {
        String color = mCategories.get(position).getColor();

        if (color.equals(Constants.CYAN)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_cyan));
        } else if(color.equals(Constants.BLUE)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_blue));
        } else if(color.equals(Constants.GREEN)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_green));
        } else if(color.equals(Constants.MAGENTA)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_magenta));
        } else if(color.equals(Constants.ORANGE)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_orange));
        } else if(color.equals(Constants.RED)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_red));
        } else if(color.equals(Constants.ROSE)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_rose));
        } else if(color.equals(Constants.VIOLET)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_violet));
        } else if(color.equals(Constants.YELLOW)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_yellow));
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
