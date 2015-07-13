package com.ver2point0.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.adapter.SavePoiListAdapter;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.util.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChangeCategoryFragment extends DialogFragment {

    private String mId;
    private String mCatName;
    private String mCatColor;
    private Category mCategory;
    private Context mContext;


    private OnFragmentInteractionListener mListener;

    public ChangeCategoryFragment() {}

    public ChangeCategoryFragment(String id, String catName, String catColor, Context context) {
        mId = id;
        mCatName = catName;
        mCatColor = catColor;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pick_category_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_save_poi_dialog));

        final Button savePoiButton = (Button) rootView.findViewById(R.id.bt_save);
        savePoiButton.setText(R.string.button_save_poi);
        if (mCategory == null) {
            savePoiButton.setEnabled(false);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPreferences.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        final ArrayList<Category> categories = new Gson().fromJson(json, type);

        ListView listView = (ListView) rootView.findViewById(R.id.lv_category_list);
        final SavePoiListAdapter adapter = new SavePoiListAdapter(mContext, categories);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                mCategory = (Category) adapterView.getItemAtPosition(position);
                savePoiButton.setEnabled(true);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}
