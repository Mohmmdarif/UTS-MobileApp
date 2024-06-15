package com.example.tanialtech.field;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tanialtech.R;
import com.example.tanialtech.field.adapter.FieldAdapter;
import com.example.tanialtech.field.data.FieldItem;

import java.util.ArrayList;


public class RecyclerViewField extends Fragment {
    private RecyclerView rvField;
    private ArrayList<FieldItem> fieldItems = new ArrayList<>();

    private FieldAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rv_field, container, false);

        // Initialize RecyclerView
        rvField = rootView.findViewById(R.id.rvField);
        rvField.setLayoutManager(new GridLayoutManager(getContext(),2));

        adapter = new FieldAdapter(getContext(), fieldItems);
        rvField.setAdapter(adapter);

        return rootView;
    }

    public void setFieldData(ArrayList<FieldItem> data) {
        // Menghapus data sebelumnya (jika ada)
        fieldItems.clear();
        // Menambahkan data baru ke dalam ArrayList
        fieldItems.addAll(data);
        // Memberi tahu adapter bahwa dataset telah berubah
        adapter.notifyDataSetChanged();
    }
}