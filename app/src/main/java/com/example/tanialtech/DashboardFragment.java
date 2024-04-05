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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        CardView cardView = view.findViewById(R.id.card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayWeatherFragment();
            }
        });

        return view;
    }

    private void displayWeatherFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();

        Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (previousFragment != null) {
            fragmentManager.beginTransaction().remove(previousFragment).commitNow();
        }

        WeatherFragment weatherFragment = new WeatherFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, weatherFragment);

        fragmentTransaction.commit();

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