package com.ver2point0.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.database.table.PoiTable;
import com.ver2point0.android.blocspot.util.Constants;
import com.ver2point0.android.blocspot.util.Utils;


public class InfoWindowFragment extends DialogFragment {

    private String mId;
    private Context mContext;
    private PoiTable mPoiTable = new PoiTable();
    private TextView mNameField;
    private TextView mNoteField;
    private ImageButton mVisitedButton;
    private TextView mCatField;

    public InfoWindowFragment() {
        // Required empty public constructor
    }

    public InfoWindowFragment(String id, Context context) {
        mId = id;
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_info_window, container, false);
       getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

       mNameField = (TextView) rootView.findViewById(R.id.tv_name_field);
       mNoteField = (TextView) rootView.findViewById(R.id.tv_note_field);
       mVisitedButton = (ImageButton) rootView.findViewById(R.id.ib_visited);
       mCatField = (TextView) rootView.findViewById(R.id.tv_category_field);

       new GetPlaceInfo(mContext, mId).execute();

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

    private class  GetPlaceInfo extends AsyncTask<Void, Void, Cursor> {

        private Context content;
        private String id;

        public GetPlaceInfo(Context context, String id) {
            this.content = context;
            this.id = id;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor = mPoiTable.poiSpecificQuery(id);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if(cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
                String note = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_NOTE));
                Boolean visited = cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COLUMN_VISITED)) > 0;
                Double lat = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE));
                Double lng = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE));
                String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
                String catName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_NAME));

                mNameField.setText(name);
                mNoteField.setText(note);
                mCatField.setText(catName);
                Utils.setColorString(color, mCatField);

                if(visited != null && visited) {
                    mVisitedButton.setImageDrawable(mContext.getResources()
                            .getDrawable(R.drawable.ic_check_on));
                }
                else if(visited != null && !visited) {
                    mVisitedButton.setImageDrawable(mContext.getResources()
                            .getDrawable(R.drawable.ic_check_off));
                }
            }
        } // end method onPostExecute()
    } // end class GetPlaceInfo
}

