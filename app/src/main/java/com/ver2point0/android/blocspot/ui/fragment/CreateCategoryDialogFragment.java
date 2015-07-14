package com.ver2point0.android.blocspot.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.category.Category;
import com.ver2point0.android.blocspot.places.Place;
import com.ver2point0.android.blocspot.util.Constants;

import java.util.ArrayList;

public class CreateCategoryDialogFragment extends DialogFragment {

    private ArrayList<Category> mCategories;
    private Place mPlace;
    private String mCategoryName;
    private Context mContext;
    private EditText mNameField;
    private RadioGroup mRadioGroup;
    private String mColorString;
    private String mId;

    public CreateCategoryDialogFragment() {}

    public CreateCategoryDialogFragment(Place place, ArrayList<Category> categories, Context context, String id) {
        mPlace = place;
        mCategories = categories;
        mContext = context;
        mId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_category_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_create_category));

        mNameField = (EditText) rootView.findViewById(R.id.et_new_category_name);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.rg_color_select);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                mColorString = setColorString(id);
            }
        });


        Button cancelButton = (Button) rootView.findViewById(R.id.bt_cancel_category);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlace != null) {
                    SavePoiDialogFragment poiDialog = new SavePoiDialogFragment(mContext, mPlace);
                    poiDialog.show(getFragmentManager(), "dialog");
                } else  {
                    ChangeCategoryFragment catDialog = new ChangeCategoryFragment(mId, mContext);
                    catDialog.show(getFragmentManager(), "dialog");
                }
                dismiss();
            }
        });


        Button createButton = (Button) rootView.findViewById(R.id.bt_add_category);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategoryName = mNameField.getText().toString();

                if(mCategoryName.equals("")) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_no_category), Toast.LENGTH_LONG).show();
                }
                if(mColorString == null) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_select_color), Toast.LENGTH_LONG).show();
                } else {
                    mCategories.add(new Category(mCategoryName, mColorString));
                    String jsonCat = new Gson().toJson(mCategories);
                    SharedPreferences.Editor prefsEditor =
                            mContext.getSharedPreferences(Constants.MAIN_PREFS, 0).edit();
                    prefsEditor.putString(Constants.CATEGORY_ARRAY, jsonCat);
                    prefsEditor.commit();

                    if (mPlace != null) {
                        SavePoiDialogFragment poiDialog = new SavePoiDialogFragment(mContext, mPlace);
                        poiDialog.show(getFragmentManager(), "dialog");
                    } else  {
                        ChangeCategoryFragment catDialog = new ChangeCategoryFragment(mId, mContext);
                        catDialog.show(getFragmentManager(), "dialog");
                    }
                    dismiss();
                }
            }
        });
        return rootView;
    }

    private String setColorString(int id) {
        if (id == R.id.rb_blue){
            return Constants.BLUE;
        }
        else  if(id == R.id.rb_green) {
            return Constants.GREEN;
        }
        else if(id == R.id.rb_magenta) {
            return Constants.MAGENTA;
        }
        else if(id == R.id.rb_orange) {
            return Constants.ORANGE;
        }
        else if(id == R.id.rb_red) {
            return Constants.RED;
        }
        else if(id == R.id.rb_rose) {
            return Constants.ROSE;
        }
        else if(id == R.id.rb_violet) {
            return Constants.VIOLET;
        }
        else if(id == R.id.rb_yellow) {
            return Constants.YELLOW;
        }
        return null;
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

