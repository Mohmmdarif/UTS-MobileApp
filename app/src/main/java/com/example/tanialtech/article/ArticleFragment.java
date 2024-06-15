package com.example.tanialtech.article;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tanialtech.DetailArticleFragment;
import com.example.tanialtech.R;
import com.example.tanialtech.article.adapter.ArticleAdapter;
import com.example.tanialtech.article.data.ArticleItem;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment {
    private ArticleAdapter articleAdapter;
    private List<ArticleItem> articleList;

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewArticle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        articleList = new ArrayList<>();
        // Add articles to the article list
        articleList.add(new ArticleItem("Dijamin Subur! Tanam Sawi Hingga...", "12 April 2024", R.drawable.pic_1));
        articleList.add(new ArticleItem("Dijamin Subur! Tanam Sawi Hingga...", "12 April 2024", R.drawable.pic_1));
        articleList.add(new ArticleItem("Dijamin Subur! Tanam Sawi Hingga...", "12 April 2024", R.drawable.pic_1));
        articleList.add(new ArticleItem("Dijamin Subur! Tanam Sawi Hingga...", "12 April 2024", R.drawable.pic_1));

        // Add more articles...

        articleAdapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(articleAdapter);

        articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArticleItem article) {
                displayDetailArticle();
            }
        });

        return view;
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
            CardView cardArticle = previousView.findViewById(R.id.card_article_for_you);
            if (cardArticle != null) {
                ViewGroup parentView = (ViewGroup) cardArticle.getParent();
                parentView.removeView(cardArticle);
            }
        }
    }
}
