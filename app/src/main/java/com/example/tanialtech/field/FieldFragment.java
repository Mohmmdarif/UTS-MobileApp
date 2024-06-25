package com.example.tanialtech.field;

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
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanialtech.R;
import com.example.tanialtech.field.data.FieldItem;
import com.example.tanialtech.profile.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FieldFragment extends Fragment {

    private String url = "https://api-simdoks.simdoks.web.id/fields";
    private ArrayList<FieldItem> fieldItems = new ArrayList<>();
    private RecyclerViewField recyclerViewField;

    public FieldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_field, container, false);

        recyclerViewField = new RecyclerViewField();

        getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, recyclerViewField).commit();

        view.post(new Runnable() {
            @Override
            public void run() {
                // Initialize with local data if necessary
                fieldItems = getFieldItems(null);
                recyclerViewField.setFieldData(fieldItems);
                getData();
            }
        });

        // show modal dialog
        RelativeLayout tambahButton = view.findViewById(R.id.tombol_tambah_ladang);
        tambahButton.setOnClickListener(v -> showFormTambahLadangDialog());

        // Search filter
        EditText searchText = view.findViewById(R.id.search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int user_id = 1; // ID pengguna yang sesuai
                searchField(user_id, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Intent to profile screen
        ImageView profile = view.findViewById(R.id.profile_icon);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);  // Tambahkan ke back stack agar bisa kembali ke fragment sebelumnya
                transaction.commit();
            }
        });

        return view;
    }

    private ArrayList<FieldItem> getFieldItems(JSONArray jsonArray) {
        ArrayList<FieldItem> fieldItems = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    int user_id = jsonObject.getInt("user_id");
                    String name = jsonObject.getString("name");
                    String code = jsonObject.getString("code");
                    int size = jsonObject.getInt("size");
                    String harvest_time = jsonObject.getString("harvest_time");
                    String planting_period = jsonObject.getString("planting_period");
                    String img_url = jsonObject.getString("img_url");
                    Boolean isSelected = false;

                    // Buat FieldItem baru dan tambahkan ke daftar
                    fieldItems.add(new FieldItem(
                            id,
                            img_url,
                            name,
                            code,
                            String.valueOf(size),
                            harvest_time,
                            planting_period,
                            isSelected
                    ));
                } catch (JSONException e) {
                    Log.e("Volley", "JSON parsing error: " + e.getMessage());
                }
            }
        }

        return fieldItems;
    }

    private void showFormTambahLadangDialog() {
        FormTambahLadangDialogFragment dialogFragment = new FormTambahLadangDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "FormTambahLadangDialogFragment");
    }

    private void getData() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    // Dapatkan data field dari API dan tambahkan ke RecyclerView
                    fieldItems = getFieldItems(jsonArray);
                    recyclerViewField.setFieldData(fieldItems);
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

    private void showEditDialog(FieldItem item) {
        FormTambahLadangDialogFragment dialogFragment = FormTambahLadangDialogFragment.newInstance(item);
        dialogFragment.show(getChildFragmentManager(), "FormEditLadangDialogFragment");
    }

    private void searchField(int user_id, String query) {
        ArrayList<FieldItem> filteredList = new ArrayList<>();
        for (FieldItem item : fieldItems) {
            if (item.getNamaLadang().toLowerCase().contains(query.toLowerCase()) ||
                    item.getKodeLadang().toLowerCase().contains(query.toLowerCase()) ||
                    item.getLuasLadang().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        recyclerViewField.setFilteredData(filteredList);
    }
}


