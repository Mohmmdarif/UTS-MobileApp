package com.example.tanialtech.article;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tanialtech.R;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailArticleFragment extends Fragment {
    private static final String ARG_ARTICLE_ID = "article_id";
    private int articleId;

    ImageView arrowBack;
    public DetailArticleFragment() {
        // Required empty public constructor
    }

    public static DetailArticleFragment newInstance(int articleId) {
        DetailArticleFragment fragment = new DetailArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getInt(ARG_ARTICLE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_article, container, false);
        arrowBack = view.findViewById(R.id.arrow_back);

        fetchArticleDetails(articleId, view);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayArticleFragment();
            }
        });

        return view;
    }

    private void displayArticleFragment(){
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.article_page);

        // Hapus semua tampilan dari ConstraintLayout
        constraintLayout.removeAllViews();

        // Buat instance dari FieldFragment
        ArticleFragment articleFragment1 = new ArticleFragment();

        // Dapatkan FragmentManager
        FragmentManager fragmentManager = getChildFragmentManager();

        // Lakukan transaksi fragment
        fragmentManager.beginTransaction()
                .replace(constraintLayout.getId(), articleFragment1)
                .addToBackStack(null) // Ini untuk menambahkan ke back stack
                .commit();
    }

    private void fetchArticleDetails(int articleId, View view) {
        String url = "https://api-simdoks.simdoks.web.id/article/" + articleId;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String title = jsonObject.getString("title");
                    String date = jsonObject.getString("date");
                    String imgUrl = jsonObject.getString("img_url");
                    String content = jsonObject.getString("content");

                    // Update UI elements
                    ((TextView) view.findViewById(R.id.detail_title)).setText(title);
                    ((TextView) view.findViewById(R.id.detail_date)).setText(date);
                    ((TextView) view.findViewById(R.id.description_text)).setText(content);

                    // Use Glide or Picasso to load the image
                    ImageView imageView = view.findViewById(R.id.image_detail);
                    Glide.with(view.getContext()).load("https://api-simdoks.simdoks.web.id/" + imgUrl).placeholder(R.drawable.pic_1)
                            .error(R.drawable.pic_1).into(imageView);

                } catch (JSONException e) {
                    Log.e("Volley", "JSON parsing error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Error fetching data: " + error.getMessage());
            }
        });

        requestQueue.add(stringRequest);
    }
}