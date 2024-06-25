package com.example.tanialtech.profile;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.tanialtech.R;
import com.example.tanialtech.article.ArticleFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragmentArticle extends Fragment {

    TextView edit, username, emailField, password, email, name;

    ImageView back, imageProfile;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;

    public ProfileFragmentArticle() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_article, container, false);

        back = view.findViewById(R.id.back);
        edit = view.findViewById(R.id.edit);
        name = view.findViewById(R.id.nama);
        imageProfile = view.findViewById(R.id.imageView);
        username = view.findViewById(R.id.namefield);
        emailField = view.findViewById(R.id.mailfield);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.passfield);

        requestQueue = Volley.newRequestQueue(requireContext());

        getProfileData();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEditProfileFragment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayArticleFragment();
            }
        });

        return view;
    }

    private void getProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1); // -1 is the default value if not found
        String accessToken = sharedPreferences.getString("accessToken", "");

        String url = "https://api-simdoks.simdoks.web.id/user/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            // Ambil data profil pengguna dari response JSON
                            String userName = response.getString("username");
                            username.setText(userName);
                            name.setText(userName);

                            String mail = response.getString("email");
                            emailField.setText(mail);
                            email.setText(mail);

                            // Cek jika imgUrl tidak null atau kosong
                            String imgUrl = response.isNull("img_url") ? null : "https://api-simdoks.simdoks.web.id/" + response.getString("img_url");

                            // Gunakan placeholder jika imgUrl kosong atau null
                            if (imgUrl != null && !imgUrl.isEmpty()) {
                                Glide.with(getContext())
                                        .load(imgUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.editfoto).transform(new CircleCrop())
                                        .into(imageProfile);
                            } else {
                                // Gunakan gambar placeholder atau tindakan pengganti lainnya
                                Glide.with(getContext()).load(R.drawable.profile).into(imageProfile);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.getMessage());
                        Toast.makeText(getActivity(), "Failed to get profile data.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken); // Menggunakan accessToken untuk otorisasi
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void displayArticleFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();

        ArticleFragment articleFragment = new ArticleFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, articleFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void displayEditProfileFragment(){
        FragmentManager fragmentManager = getParentFragmentManager();
        EditProfileFragment editProfileFragment = new EditProfileFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, editProfileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }
}