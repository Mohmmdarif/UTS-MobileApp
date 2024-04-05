package com.example.tanialtech;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.tanialtech.field.FormTambahLadangDialogFragment;


public class ActivityFragment extends Fragment {


    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        RelativeLayout tambahKegiatan = view.findViewById(R.id.tombol_tambah_kegiatan);

        tambahKegiatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFormTambahKegiatanDialog();
            }
        });

        return view;
    }

    private void showFormTambahKegiatanDialog() {
        FormTambahKegiatanDialog dialogFragment = new FormTambahKegiatanDialog();
        dialogFragment.show(getChildFragmentManager(),"FormTambahKegiatanDialog");
    }
}