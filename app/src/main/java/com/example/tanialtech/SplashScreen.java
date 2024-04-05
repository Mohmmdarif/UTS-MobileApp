package com.example.tanialtech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    Button tombolDaftar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageCarousel carousel = findViewById(R.id.carousel);

        carousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem(R.drawable.ibuibu));
        list.add(new CarouselItem(R.drawable.ibuibu));
        list.add(new CarouselItem(R.drawable.ibuibu));
        carousel.setData(list);
        carousel.setAutoPlay(true);
        carousel.setShowNavigationButtons(false);

        tombolDaftar = findViewById(R.id.tombolDaftar);

        tombolDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashScreen.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}