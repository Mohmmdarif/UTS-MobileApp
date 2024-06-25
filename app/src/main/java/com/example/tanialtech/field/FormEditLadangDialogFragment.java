package com.example.tanialtech.field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tanialtech.R;
import com.example.tanialtech.field.data.FieldItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormEditLadangDialogFragment extends DialogFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ARG_FIELD_ITEM = "field_item";
    private EditText inputNamaLadang, inputKodeLadang, inputLuasLadang, inputPerkiraanLadang;
    private ImageView fotoLadang;
    private Uri imageUri;
    private Bitmap bitmap;
    private FieldItem fieldItem;
    private boolean isImageChanged = false;
    SharedPreferences sharedPreferences;

    public static FormEditLadangDialogFragment newInstance(FieldItem item) {
        FormEditLadangDialogFragment fragment = new FormEditLadangDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FIELD_ITEM, (Parcelable) item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fieldItem = getArguments().getParcelable(ARG_FIELD_ITEM);
        }
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_edit_ladang_dialog, container, false);

        inputNamaLadang = view.findViewById(R.id.inputNamaLadang);
        inputKodeLadang = view.findViewById(R.id.inputKodeLadang);
        inputLuasLadang = view.findViewById(R.id.inputLuasLadang);
        inputPerkiraanLadang = view.findViewById(R.id.inputPerkiraanLadang);
        fotoLadang = view.findViewById(R.id.tombolTambah);
        fotoLadang.setOnClickListener(v -> chooseImage());

        view.findViewById(R.id.batal).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.simpan).setOnClickListener(v -> {
            if (validateForm()) {
                PutFieldForm();
            }
        });

        if (fieldItem != null) {
            inputNamaLadang.setText(fieldItem.getNamaLadang());
            inputKodeLadang.setText(fieldItem.getKodeLadang());
            inputLuasLadang.setText(fieldItem.getLuasLadang());
            inputPerkiraanLadang.setText(fieldItem.getPlanting_period_convert());

            Glide.with(this)
                    .asBitmap() // Request as bitmap
                    .load("https://api-simdoks.simdoks.web.id/" + fieldItem.getImageResource())
                    .placeholder(R.drawable.pic_1)
                    .error(R.drawable.pic_1)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource; // Store the bitmap
                            fotoLadang.setImageBitmap(bitmap); // Display the image
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This can be left empty
                        }
                    });
        }

        return view;
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(inputNamaLadang.getText().toString().trim())) {
            inputNamaLadang.setError("Nama Ladang harus diisi");
            valid = false;
        }

        if (TextUtils.isEmpty(inputKodeLadang.getText().toString().trim())) {
            inputKodeLadang.setError("Kode Ladang harus diisi");
            valid = false;
        }

        if (TextUtils.isEmpty(inputLuasLadang.getText().toString().trim())) {
            inputLuasLadang.setError("Luas Ladang harus diisi");
            valid = false;
        }

        if (TextUtils.isEmpty(inputPerkiraanLadang.getText().toString().trim())) {
            inputPerkiraanLadang.setError("Perkiraan Masa Tanam harus diisi");
            valid = false;
        }

        Log.d("Validation", "Form validation result: " + valid);
        return valid;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            isImageChanged = true; // Set flag to true when a new image is selected
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                fotoLadang.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void PutFieldForm() {
        int userId = sharedPreferences.getInt("user_id", -1); // -1 is the default value if not found

        String url = "https://api-simdoks.simdoks.web.id/field/" + fieldItem.getId(); // URL API dengan ID field

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String responseString = new String(response.data);
                        Log.d("VolleyResponse", responseString);
                        // Handle response dari server
                        Toast.makeText(getActivity(), "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String errorResponse = new String(networkResponse.data);
                            Log.e("VolleyError", errorResponse);
                            Toast.makeText(getActivity(), "Gagal memperbarui data!: " + errorResponse, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("VolleyError", "Unknown error occurred: " + error.getMessage());
                            Toast.makeText(getActivity(), "Gagal memperbarui data!: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("name", inputNamaLadang.getText().toString().trim());
                params.put("code", inputKodeLadang.getText().toString().trim());
                params.put("size", inputLuasLadang.getText().toString().trim());
                params.put("planting_period", inputPerkiraanLadang.getText().toString().trim());
                Log.d("VolleyParams", params.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                if (bitmap != null) {
                    Log.d("VolleyParams", "Image included in request");
                    params.put("img", new DataPart("ladang_image.jpg", getFileDataFromDrawable(bitmap)));
                } else {
                    Log.d("VolleyParams", "No image included in request");
                }
                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(multipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
