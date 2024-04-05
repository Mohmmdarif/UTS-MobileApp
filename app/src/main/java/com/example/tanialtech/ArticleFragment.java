package com.example.tanialtech;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ArticleFragment extends Fragment {

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);

        CardView cardArticleForYou1 = rootView.findViewById(R.id.card_article_for_you_1);

        cardArticleForYou1.setOnClickListener(view -> {
            Fragment DetailArticleFragment = new DetailArticleFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_article_page, DetailArticleFragment)
                    .commit();
        });

        return rootView;
    }
}