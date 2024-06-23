package com.example.tanialtech;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.tanialtech.FormTambahKegiatanDialog;
import com.example.tanialtech.R;
import com.example.tanialtech.profile.ProfileFragment;


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


        // Intent to profile screen
        ImageView profile = view.findViewById(R.id.profile_icon);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);  // Tambahkan ke back stack agar bisa kembali ke fragment sebelumnya
                transaction.commit();
            }
        });

        return view;
    }


    private void showFormTambahKegiatanDialog() {
        FormTambahKegiatanDialog dialogFragment = new FormTambahKegiatanDialog();
        dialogFragment.show(getChildFragmentManager(),"FormTambahKegiatanDialog");
    }
}