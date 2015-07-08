package com.ver2point0.android.blocspot.ui.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.adapter.SavePoiListAdapter;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.database.table.PoiTable;
import com.ver2point0.android.blocspot.places.Place;
import com.ver2point0.android.blocspot.util.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavePoiDialogFragment extends DialogFragment {

    private Place mPlace;
    private ListView mListView;
    private Context mContext;
    private Category mCategory;
    private PoiTable mPoiTable = new PoiTable();

    public SavePoiDialogFragment() {}

    public SavePoiDialogFragment(Context context, Place place) {
        mContext = context;
        mPlace = place;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_poi_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_save_poi_dialog));


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPreferences.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        final ArrayList<Category> categories = new Gson().fromJson(json, type);

        mListView = (ListView) rootView.findViewById(R.id.lv_category_list);
        final SavePoiListAdapter adapter = new SavePoiListAdapter(mContext, categories);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                mCategory = (Category) adapterView.getItemAtPosition(position);
            }
        });

        Button categoryButton = (Button) rootView.findViewById(R.id.bt_add);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCategoryDialogFragment dialogFragment =
                        new CreateCategoryDialogFragment(mPlace, categories, mContext);
                dialogFragment.show(getFragmentManager(), "dialog");
                dismiss();
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.bt_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button savePoiButton = (Button) rootView.findViewById(R.id.bt_save);
        savePoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mPlace.getName();
                final Double lat = mPlace.getLatitude();
                final Double lng = mPlace.getLongitude();
                final String catName = mCategory.getName();
                final String catColor = mCategory.getColor();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mPoiTable.addNewPoi(name, lat, lng, catName, catColor);
                    }
                }.start();
                Toast.makeText(mContext, mContext.getString(R.string.toast_poi_saved), Toast.LENGTH_LONG).show();
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {}
}
