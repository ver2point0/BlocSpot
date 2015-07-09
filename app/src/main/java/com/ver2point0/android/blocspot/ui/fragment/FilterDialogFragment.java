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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.adapter.SavePoiListAdapter;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.ui.activity.BlocSpotActivity;
import com.ver2point0.android.blocspot.util.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FilterDialogFragment extends DialogFragment {

    private Context mContext;
    private Category mCategory;

    private OnFilterListener mFilterListener;

    public FilterDialogFragment() {}

    public FilterDialogFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_filter));

        final Button filterButton = (Button) rootView.findViewById(R.id.bt_filter);
        if (mCategory == null) {
            filterButton.setEnabled(false);
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
                filterButton.setEnabled(true);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mCategory.getName();
                ((BlocSpotActivity) mContext).applyFilters(name);
                dismiss();
            }
        });

        Button resetButton = (Button) rootView.findViewById(R.id.bt_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BlocSpotActivity) mContext).applyFilters(null);
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFilterListener = (OnFilterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
            " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFilterListener = null;
    }

    public interface OnFilterListener {
        public void applyFilters(String name);
    }
}
