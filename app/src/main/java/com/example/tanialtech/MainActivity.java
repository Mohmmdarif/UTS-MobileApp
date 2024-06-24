package com.example.tanialtech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.tanialtech.Dashboard.DashboardFragment;
import com.example.tanialtech.activity.ActivityFragment;
import com.example.tanialtech.article.ArticleFragment;
import com.example.tanialtech.field.FieldFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.navigation_menu);
        bottomNavigationView.setSelectedItemId(R.id.dashboard_icon);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_fragment, new DashboardFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment selectedFragment;

                if (item.getItemId() == R.id.dashboard_icon) {
                        selectedFragment = new DashboardFragment();
                } else if (item.getItemId() == R.id.activity_icon) {
                    selectedFragment = new ActivityFragment();
                } else if (item.getItemId() == R.id.field_icon) {
                    selectedFragment = new FieldFragment();
                } else if (item.getItemId() == R.id.article_icon) {
                    selectedFragment = new ArticleFragment();
                } else {
                    return false;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_fragment, selectedFragment).commit();
                return true;
        });
    }
}
