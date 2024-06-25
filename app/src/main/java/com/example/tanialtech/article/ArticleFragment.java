package com.example.tanialtech.article;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanialtech.R;
import com.example.tanialtech.article.adapter.ArticleAdapter;
import com.example.tanialtech.article.adapter.MoreArticleAdapter;
import com.example.tanialtech.article.data.ArticleItem;
import com.example.tanialtech.article.data.MoreArticle;
import com.example.tanialtech.profile.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleFragment extends Fragment {
    private ArrayList<ArticleItem> articleItems = new ArrayList<>();
    private ArrayList<MoreArticle> moreArticleItems = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private MoreArticleAdapter moreArticleAdapter;
    SharedPreferences sharedPreferences;

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        // Recyclerview 1 (article)
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewArticle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Recyclerview 2 (detail article)
        RecyclerView recyclerView1 = view.findViewById(R.id.more_article);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // Set Adapter 1
        articleAdapter = new ArticleAdapter(articleItems);
        recyclerView.setAdapter(articleAdapter);

        // Fetch data from API
        getDataAllArticle();

        // Set Adapter 2
        moreArticleAdapter = new MoreArticleAdapter(moreArticleItems);
        recyclerView1.setAdapter(moreArticleAdapter);

        articleAdapter.setOnItemClickListener(article -> displayDetailArticle(article.getId()));
        moreArticleAdapter.setOnItemClickListener(article1 -> displayDetailArticle(article1.getId()));

        // Search filter
        EditText searchText = view.findViewById(R.id.search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.isEmpty()) {
                    articleAdapter.updateData(articleItems); // Panggil metode untuk mengembalikan daftar artikel
                } else {
                    articleAdapter.filter(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ImageView profile = view.findViewById(R.id.profile_icon);
        profile.setOnClickListener(v -> {
            ConstraintLayout constraintLayout = requireView().findViewById(R.id.article_page);
            constraintLayout.removeAllViews();
            ProfileFragment profileFragment = new ProfileFragment();

            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(constraintLayout.getId(), profileFragment)
                    .addToBackStack(null) // Ini untuk menambahkan ke back stack
                    .commit();

        });

        return view;
    }

    private void displayDetailArticle(int articleId) {
        FragmentManager fragmentManager = getParentFragmentManager();

        Fragment previousFragment = fragmentManager.findFragmentById(R.id.article_page);
        if (previousFragment != null) {
            fragmentManager.beginTransaction().remove(previousFragment).commitNow();
        }

        DetailArticleFragment detailArticle = DetailArticleFragment.newInstance(articleId);

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

    private void getDataAllArticle() {
        String url = "https://api-simdoks.simdoks.web.id/articles";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    // Dapatkan data field dari API dan tambahkan ke RecyclerView
                    articleItems = getArticleItems(jsonArray);
                    moreArticleItems = getMoreArticleItems(jsonArray);
                    articleAdapter.updateData(articleItems);
                    moreArticleAdapter.updateData(moreArticleItems);
                } catch (JSONException e) {
                    Log.e("Volley", "JSON parsing error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Error fetching data: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + Base64.encodeToString("ibnunazm:ibnunazm.a11".getBytes(), Base64.NO_WRAP));
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private ArrayList<ArticleItem> getArticleItems(JSONArray jsonArray) {
        ArrayList<ArticleItem> items = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String date = jsonObject.getString("date");
                    String imgUrl = jsonObject.getString("img_url");

                    // Create a new ArticleItem and add it to the list
                    items.add(new ArticleItem(id, title, date, imgUrl));
                } catch (JSONException e) {
                    Log.e("Volley", "JSON parsing error: " + e.getMessage());
                }
            }
        }

        return items;
    }


    private ArrayList<MoreArticle> getMoreArticleItems(JSONArray jsonArray) {
        ArrayList<MoreArticle> moreItems = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String date = jsonObject.getString("date");
                    String imgUrl = jsonObject.getString("img_url");
                    String content = jsonObject.getString("content");

                    // Create a new ArticleItem and add it to the list
                    moreItems.add(new MoreArticle(id, imgUrl, title, content, date));
                } catch (JSONException e) {
                    Log.e("Volley", "JSON parsing error: " + e.getMessage());
                }
            }
        }

        return moreItems;
    }
}
