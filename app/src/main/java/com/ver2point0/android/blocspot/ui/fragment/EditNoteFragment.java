package com.ver2point0.android.blocspot.ui.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ver2point0.android.blocspot.R;
import com.ver2point0.android.blocspot.ui.activity.BlocSpotActivity;
import com.ver2point0.android.blocspot.util.Constants;

public class EditNoteFragment extends DialogFragment {

    private String mOldNote;
    private String mId;
    private Context mContext;
    private EditText mNewNote;
    private OnNoteUpdateListener mListener;

    public EditNoteFragment() {}

    @SuppressLint("ValidFragment")
    public EditNoteFragment(String id, Context context, String note) {
        mId = id;
        mContext = context;
        mOldNote = note;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.EDIT_NOTE_TEXT, mNewNote.getText().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_note, container, false);
        getDialog().setTitle(getString(R.string.title_edit_note));
        getDialog().setCanceledOnTouchOutside(true);

        if (savedInstanceState != null) {
            mOldNote = savedInstanceState.getString(Constants.EDIT_NOTE_TEXT);
        }

        mNewNote = (EditText) rootView.findViewById(R.id.et_edit_note);
        mNewNote.setText(mOldNote);

        Button cancelButton = (Button) rootView.findViewById(R.id.bt_edit_note_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button updateButton = (Button) rootView.findViewById(R.id.bt_edit_note);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedNote = mNewNote.getText().toString();
                ((BlocSpotActivity) mContext).updateNoteDataBase(mId, updatedNote);
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNoteUpdateListener) activity;
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

    public interface OnNoteUpdateListener {
        public void updateNoteDataBase(String name, String note);
    }
}
