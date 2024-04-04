package com.example.tanialtech;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;



public class DashboardFragment extends Fragment {

    public DashboardFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Temukan card view dan atur onClickListener
        CardView cardView = view.findViewById(R.id.card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil metode untuk menampilkan fragment_weather.xml
                displayWeatherFragment();
            }
        });

        return view;
    }

    private void displayWeatherFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();

        // Hapus fragment sebelumnya (jika ada)
        Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (previousFragment != null) {
            fragmentManager.beginTransaction().remove(previousFragment).commitNow();
        }

        // Buat objek WeatherFragment
        WeatherFragment weatherFragment = new WeatherFragment();

        // Mulai transaksi fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Ganti fragment saat ini dengan fragment_weather.xml
        fragmentTransaction.replace(R.id.fragment_container, weatherFragment);

        // Tidak menambahkan transaksi ke back stack
        // fragmentTransaction.addToBackStack(null); // Hapus baris ini

        // Komit transaksi
        fragmentTransaction.commit();

        // Menghapus CardView dengan id "card" dari fragment sebelumnya
        View previousView = getView();
        if (previousView != null) {
            CardView cardView = previousView.findViewById(R.id.card);
            if (cardView != null) {
                ViewGroup parentView = (ViewGroup) cardView.getParent();
                parentView.removeView(cardView);
            }
        }
    }
}