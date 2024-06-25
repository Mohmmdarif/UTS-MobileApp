package com.example.tanialtech.activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanialtech.R;
import com.example.tanialtech.login_register.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;

    Button btnSave;

    private LocalTime time;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        int userId = sharedPreferences.getInt("user_id", -1);

        requestQueue = Volley.newRequestQueue(this);

        initWidgets();
//        getProfileData();
        btnSave = findViewById(R.id.save);

        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = eventNameET.getText().toString().trim();
                String eventDateText = "Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate);
                String date = eventDateText.substring(6);
                String time = eventTimeTV.getText().toString().trim();
                Boolean status = false;

                if (name.isEmpty()) {
                    Toast.makeText(EventEditActivity.this, "Nama Kegiatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (time.isEmpty()) {
                    Toast.makeText(EventEditActivity.this, "Waktu tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    SimpleDateFormat originalFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date originalDate = originalFormat.parse(date);
                    String formattedDate = targetFormat.format(originalDate);

                    createActivity(name, formattedDate, time, status);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EventEditActivity.this, "Error formatting date", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeET);

    }


    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
        String eventTime = eventTimeTV.getText().toString();
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, eventTime);
        Event.eventsList.add(newEvent);
        finish();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EventEditActivity.this, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.getMessage());
                        Log.e("API Error", "Response Data: " + new String(error.networkResponse.data));
                        Toast.makeText(EventEditActivity.this, "Failed to get profile data.", Toast.LENGTH_SHORT).show();
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

    private void createActivity(String name, String date, String time, Boolean status) {
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");

        // Cek apakah user_id valid
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID. Please log in again.", Toast.LENGTH_SHORT).show();
            // Arahkan pengguna kembali ke layar login atau ambil tindakan yang sesuai
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String url = "https://api-simdoks.simdoks.web.id/todo";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("date", date);
            jsonBody.put("time", time);
            jsonBody.put("status", status);
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log JSON request sebelum mengirimkan
        Log.d("JSON Request", jsonBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Mengganti fragment setelah berhasil membuat aktivitas
                        Toast.makeText(EventEditActivity.this, "Data Behasil ditambah", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                            if (error.networkResponse.data != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    Log.e("Volley Error", "Response Body: " + responseBody);
                                    JSONObject data = new JSONObject(responseBody);
                                    String errorMessage = data.getString("message"); // atau gunakan pesan kesalahan lain dari respons JSON
                                    Toast.makeText(EventEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } catch (JSONException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    Toast.makeText(EventEditActivity.this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Volley Error", "Network response data is null");
                                Toast.makeText(EventEditActivity.this, "Network response data is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Volley Error", "Network response is null");
                            Toast.makeText(EventEditActivity.this, "Network response is null", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("Volley Error", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken); // Tambahkan header Authorization
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }



}