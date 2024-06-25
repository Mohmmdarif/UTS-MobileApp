package com.example.tanialtech.field;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanialtech.R;
import com.example.tanialtech.field.adapter.FieldAdapter;
import com.example.tanialtech.field.data.FieldItem;
import com.example.tanialtech.profile.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FieldFragment extends Fragment {
    private FieldAdapter adapter;
    private ArrayList<FieldItem> fieldItems = new ArrayList<>();
    private RecyclerViewField recyclerViewField;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;


    public FieldFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    @SuppressLint("WrongViewCast")
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

//        getData();

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
                int user_id = sharedPreferences.getInt("user_id", -1); // ID pengguna yang sesuai
                searchField(user_id, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Intent to profile screen
        ImageView profile = view.findViewById(R.id.profile_icon);
        profile.setOnClickListener(v -> {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ProfileFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Add delete button and set click listener
        ImageView deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> deleteSelectedFields());

        return view;
    }

    private void deleteSelectedFields() {
        List<FieldItem> selectedItems = recyclerViewField.getSelectedItems();
        for (FieldItem item : selectedItems) {
            deleteField(item.getId());
        }
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
                    String planting_period = jsonObject.getString("planting_period");
                    String harvest_time = jsonObject.getString("harvest_time");
                    String img_url = jsonObject.getString("img_url");
                    boolean isSelected = false;

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
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");// -1 is the default value if not found

        String url = "https://api-simdoks.simdoks.web.id/fields/user/" + userId;

        requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    // Dapatkan data field dari API dan tambahkan ke RecyclerView
                    fieldItems = getFieldItems(jsonArray);
                    Log.d("FieldFragment", "Data fetched: " + fieldItems.size());
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
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
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

    private void deleteField(int fieldId) {
        String url = "https://api-simdoks.simdoks.web.id/field/" + fieldId;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, response -> {
            // Handle successful response
            Toast.makeText(getActivity(), "Data dengan id: " + fieldId + " berhasil dihapus!", Toast.LENGTH_SHORT).show();
            getData(); // Refresh data after deletion
        }, error -> {
            Log.e("Volley", "Error deleting data: " + error.getMessage());
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


}