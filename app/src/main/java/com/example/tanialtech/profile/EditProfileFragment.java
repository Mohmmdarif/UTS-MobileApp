package com.example.tanialtech.profile;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private boolean isPasswordVisible = false;
    private EditText passwordEditText;

    EditText emailEditText, unameEditext;

    TextView name, mail, save;

    ImageView back, editImage, imageProfile;

    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        imageProfile = view.findViewById(R.id.imageView);
        back = view.findViewById(R.id.back);
        save = view.findViewById(R.id.save);
        name = view.findViewById(R.id.nama);
        mail = view.findViewById(R.id.email);
        unameEditext = view.findViewById(R.id.editname);
        emailEditText = view.findViewById(R.id.editmail);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        editImage = view.findViewById(R.id.editImage);
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);

        getProfileData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        return view;
    }

    private void getProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");

        String url = "https://api-simdoks.simdoks.web.id/user/" + userId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            String username = response.getString("username");
                            String email = response.getString("email");
                            String imageUrl = response.isNull("img_url") ? null : "https://api-simdoks.simdoks.web.id/" + response.getString("img_url");

                            unameEditext.setText(username);
                            emailEditText.setText(email);
                            name.setText(username);
                            mail.setText(email);

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(getContext())
                                        .load(imageUrl)
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
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Error Response code: " + error.networkResponse.statusCode);
                        }
                        Toast.makeText(getActivity(), "Failed to get profile data.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }


    private void saveProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");

        String url = "https://api-simdoks.simdoks.web.id/user/" + userId;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", unameEditext.getText().toString().trim());
            jsonBody.put("email", emailEditText.getText().toString().trim());
            String password = passwordEditText.getText().toString().trim();
            if (!password.isEmpty()) {
                jsonBody.put("password", password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        int userId = sharedPreferences.getInt("user_id", -1);
        String url = "https://api-simdoks.simdoks.web.id/user/upload-image/" + userId;
        String accessToken = sharedPreferences.getString("accessToken", "");

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                // Panggil method untuk mengambil ulang data profil setelah gambar diunggah
                                getProfileData();
                            } else {
                                Toast.makeText(getActivity(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Failed to parse response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Upload Error", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("Upload Error", "Response Data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(getActivity(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    byte[] fileBytes = IOUtils.toByteArray(inputStream);
                    params.put("img", new DataPart("profile_image.jpg", fileBytes, "image/jpeg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(volleyMultipartRequest);
    }




    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }



    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
        }
        passwordEditText.setSelection(passwordEditText.length());
        isPasswordVisible = !isPasswordVisible;
    }
}
