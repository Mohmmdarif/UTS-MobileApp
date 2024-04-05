package com.example.tanialtech.field;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.tanialtech.R;
import com.example.tanialtech.field.data.FieldItem;

import java.util.ArrayList;


public class FieldFragment extends Fragment {

    public FieldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_field, container, false);

        FrameLayout frameLayout = view.findViewById(R.id.frameLayout);

        RecyclerViewField recyclerViewField = new RecyclerViewField();

        getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, recyclerViewField).commit();

        view.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<FieldItem> fieldItems = getFieldItems();
                recyclerViewField.setFieldData(fieldItems);
            }
        });

        RelativeLayout tambahButton = view.findViewById(R.id.tombol_tambah_ladang);
        tambahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormTambahLadangDialog();
            }
        });


        return view;
    }
    private ArrayList<FieldItem> getFieldItems() {
        ArrayList<FieldItem> fieldItems = new ArrayList<>();
        fieldItems.add(new FieldItem(R.drawable.pic_1,"Ladang 1","S-01","120","30"));
        fieldItems.add(new FieldItem(R.drawable.pic_1,"Ladang 2","S-02","130","40"));
        fieldItems.add(new FieldItem(R.drawable.pic_1,"Ladang 3","S-03","110","27"));
        fieldItems.add(new FieldItem(R.drawable.pic_1,"Ladang tetangga","S-04","110","27"));

        return fieldItems;
    }
    private void showFormTambahLadangDialog() {
        FormTambahLadangDialogFragment dialogFragment = new FormTambahLadangDialogFragment();
        dialogFragment.show(getChildFragmentManager(),"FormTambahLadangDialogFragment");
    }
}