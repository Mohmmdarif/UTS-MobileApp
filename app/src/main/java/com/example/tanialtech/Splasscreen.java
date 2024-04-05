package com.example.tanialtech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class Splasscreen extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    CarouselView carouselView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasscreen);

        carouselView = findViewById(R.id.carouselView);
        images.add(R.drawable.img1);
        images.add(R.drawable.img1);
        images.add(R.drawable.img1);

        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);
        carouselView.setImageClickListener(clickListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(images.get(position));
        }
    };

    ImageClickListener clickListener  = new ImageClickListener() {
        @Override
        public void onClick(int position) {
            Toast.makeText(Splasscreen.this, "Click image-" + String.valueOf(position + 1), Toast.LENGTH_SHORT).show();
        }
    };
}