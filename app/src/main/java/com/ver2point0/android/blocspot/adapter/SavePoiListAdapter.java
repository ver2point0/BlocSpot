package com.ver2point0.android.blocspot.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.ArrayList;

public class SavePoiListAdapter extends ArrayAdapter<Category> {

    private ArrayList<Category> mCategories;
    private Context mContext;

    public SavePoiListAdapter(Context context, ArrayList<Category> categories) {
        super(context, R.layout.adapter_save_poi, categories);
        mContext = context;
        mCategories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_poi, null);
            holder = new ViewHolder();
            holder.categoryText = (TextView) convertView.findViewById(R.id.tv_category_text);
            holder.background = (RelativeLayout) convertView.findViewById(R.id.layoutBackground);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.categoryText.setText(mCategories.get(position).getName());
        setColor(position, mCategories, holder.background);

        return convertView;
    }

    private void setColor(int position, ArrayList<Category> categories, RelativeLayout background) {
        String color = mCategories.get(position).getColor();

        if (color.equals(Constants.CYAN)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.cyan));
        } else if(color.equals(Constants.BLUE)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        } else if(color.equals(Constants.GREEN)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        } else if(color.equals(Constants.MAGENTA)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.magenta));
        } else if(color.equals(Constants.ORANGE)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
        } else if(color.equals(Constants.RED)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        } else if(color.equals(Constants.ROSE)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.rose));
        } else if(color.equals(Constants.VIOLET)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.violet));
        } else if(color.equals(Constants.YELLOW)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
        }
    }

    private static class ViewHolder {
        TextView categoryText;
        RelativeLayout background;
    }

}
