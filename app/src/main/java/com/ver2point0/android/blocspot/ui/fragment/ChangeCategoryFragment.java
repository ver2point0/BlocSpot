package com.ver2point0.android.blocspot.ui.fragment;

import android.annotation.SuppressLint;
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
import com.ver2point0.android.blocspot.ui.activity.BlocSpotActivity;
import com.ver2point0.android.blocspot.util.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChangeCategoryFragment extends DialogFragment {

    private String mId;
    private Category mCategory;
    private Context mContext;
    private PoiTable mPoiTable = new PoiTable();

    public ChangeCategoryFragment() {}

    @SuppressLint("ValidFragment")
    public ChangeCategoryFragment(String id, Context context) {
        mId = id;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pick_category_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_save_poi_dialog));
        getDialog().setCanceledOnTouchOutside(true);

        final Button savePoiButton = (Button) rootView.findViewById(R.id.bt_save);
        savePoiButton.setText(mContext.getString(R.string.button_change_category));
        if (mCategory == null) {
            savePoiButton.setEnabled(false);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPreferences.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        final ArrayList<Category> categories = new Gson().fromJson(json, type);

        ListView listView = (ListView) rootView.findViewById(R.id.lv_category_list_1);
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

        Button newCatButton = (Button) rootView.findViewById(R.id.bt_add);
        newCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCategoryDialogFragment dialogFragment =
                        new CreateCategoryDialogFragment(null, categories, mContext, mId);
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

        savePoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String catName = mCategory.getName();
                final String catColor = mCategory.getColor();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mPoiTable.updateCategory(mId, catName, catColor);
                    }
                }.start();
                Toast.makeText(mContext, mContext.getString(R.string.toast_poi_updated), Toast.LENGTH_LONG).show();
                ((BlocSpotActivity) mContext).refreshList(mId);
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnChangeCategoryListener{
        public void refreshList(String id);
    }
}
