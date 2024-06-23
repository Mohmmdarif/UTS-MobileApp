package com.example.tanialtech.field;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tanialtech.R;
import com.example.tanialtech.field.VolleyMultipartRequest;
import com.example.tanialtech.field.data.FieldItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormTambahLadangDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ARG_FIELD_ITEM = "field_item";
    private EditText inputNamaLadang, inputKodeLadang, inputLuasLadang, inputPerkiraanLadang;
    private ImageView fotoLadang;
    private Uri imageUri;
    private Bitmap bitmap;
    private FieldItem fieldItem;

    public static FormTambahLadangDialogFragment newInstance(FieldItem item) {
        FormTambahLadangDialogFragment fragment = new FormTambahLadangDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FIELD_ITEM, (Parcelable) item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedDialogTheme);

        if (getArguments() != null) {
            fieldItem = getArguments().getParcelable(ARG_FIELD_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_tambah_ladang_dialog, container, false);

        inputNamaLadang = view.findViewById(R.id.inputNamaLadang);
        inputKodeLadang = view.findViewById(R.id.inputKodeLadang);
        inputLuasLadang = view.findViewById(R.id.inputLuasLadang);
        inputPerkiraanLadang = view.findViewById(R.id.inputPerkiraanLadang);
        fotoLadang = view.findViewById(R.id.tombolTambah);
        fotoLadang.setOnClickListener(v -> chooseImage());

        view.findViewById(R.id.batal).setOnClickListener(v -> {
            dismiss();
            clearForm();
        });
        view.findViewById(R.id.simpan).setOnClickListener(v -> {
            if (validateForm()) {
                PostFieldForm();
            }
        });

        // mengambil inputan sebelumnya
        if (fieldItem != null) {
            inputNamaLadang.setText(fieldItem.getNamaLadang());
            inputKodeLadang.setText(fieldItem.getKodeLadang());
            inputLuasLadang.setText(fieldItem.getLuasLadang());
            inputPerkiraanLadang.setText(fieldItem.getPerkiraanMasaTanam());

            Glide.with(this)
                    .load("https://api-simdoks.simdoks.web.id/" + fieldItem.getImageResource())
                    .placeholder(R.drawable.pic_1)
                    .error(R.drawable.pic_1)
                    .into(fotoLadang);
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

        if (bitmap == null) {
            Toast.makeText(getActivity(), "Foto Ladang harus dipilih", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }


    private void clearForm() {
        inputNamaLadang.setText("");
        inputKodeLadang.setText("");
        inputLuasLadang.setText("");
        inputPerkiraanLadang.setText("");
        fotoLadang.setImageResource(R.drawable.plus1); // Gambar default
        bitmap = null;
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
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                fotoLadang.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void PostFieldForm() {
        String url = "https://api-simdoks.simdoks.web.id/field"; // URL Api

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        // Handle response dari server
                        Toast.makeText(getActivity(), "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        clearForm();
                        dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getActivity(), "Gagal menambahkan data!: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(1));
                params.put("name", inputNamaLadang.getText().toString().trim());
                params.put("code", inputKodeLadang.getText().toString().trim());
                params.put("size", inputLuasLadang.getText().toString().trim());
                params.put("planting_period", inputPerkiraanLadang.getText().toString().trim());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                if (bitmap != null) {
                    params.put("img", new DataPart("ladang_image.jpg", getFileDataFromDrawable(bitmap)));
                }
                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(multipartRequest);
    }

    private void PutFieldForm() {
        String url = "https://api-simdoks.simdoks.web.id/field/" + fieldItem.getId(); // URL API dengan ID field

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        // Handle response dari server
                        Toast.makeText(getActivity(), "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                        clearForm();
                        dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getActivity(), "Gagal memperbarui data!: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(1));
                params.put("name", inputNamaLadang.getText().toString().trim());
                params.put("code", inputKodeLadang.getText().toString().trim());
                params.put("size", inputLuasLadang.getText().toString().trim());
                params.put("planting_period", inputPerkiraanLadang.getText().toString().trim());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                if (bitmap != null) {
                    params.put("img", new DataPart("ladang_image.jpg", getFileDataFromDrawable(bitmap)));
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
