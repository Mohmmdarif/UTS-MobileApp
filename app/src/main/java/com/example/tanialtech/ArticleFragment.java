package com.example.tanialtech;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

        cardArticleForYou1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetailArticle();
            }

        });

        return rootView;
    }

    private void displayDetailArticle() {
        FragmentManager fragmentManager = getParentFragmentManager();

        Fragment previousFragment = fragmentManager.findFragmentById(R.id.article_page);
        if (previousFragment != null) {
            fragmentManager.beginTransaction().remove(previousFragment).commitNow();
        }

        DetailArticleFragment detailArticle = new DetailArticleFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.article_page, detailArticle);

        fragmentTransaction.commit();

        View previousView = getView();
        if (previousView != null) {
            CardView cardArticle = previousView.findViewById(R.id.card_article_for_you_1);
            if (cardArticle != null) {
                ViewGroup parentView = (ViewGroup) cardArticle.getParent();
                parentView.removeView(cardArticle);
            }
        }
    }
}