package com.example.tanialtech.field;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.example.tanialtech.R;
import com.example.tanialtech.field.data.FieldItem;


public class FormEditLadangDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ARG_FIELD_ITEM = "field_item";
    private EditText inputNamaLadang, inputKodeLadang, inputLuasLadang, inputPerkiraanLadang;
    private ImageView fotoLadang;
    private Uri imageUri;
    private Bitmap bitmap;
    private FieldItem fieldItem;

    public FormEditLadangDialogFragment() {
        // Required empty public constructor
    }

    public static FormEditLadangDialogFragment newInstance(FieldItem item) {
        FormEditLadangDialogFragment fragment = new FormEditLadangDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FIELD_ITEM, (Parcelable) item);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_edit_ladang_dialog, container, false);
    }
}