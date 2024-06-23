package com.example.tanialtech.login_register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.tanialtech.MainActivity;
import com.example.tanialtech.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextView tombolMasuk, daftar;

    TextInputEditText unameEditText, passwordEditText;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tombolMasuk = findViewById(R.id.tombolMasuk);
        daftar = findViewById(R.id.daftar);

        unameEditText = findViewById(R.id.unameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tombolMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = unameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(username,password);
            }
        });
    }

    private void loginUser(String username, String password) {
        String url = Db_Contract.login;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log JSON request sebelum mengirimkan
        Log.d("JSON Request", jsonBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String accessToken = response.getString("accessToken");
                            int userId = response.getInt("user_id");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", accessToken);
                            editor.putInt("user_id", userId);
                            editor.apply();

                            Toast.makeText(RegisterActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Jika login berhasil, lanjutkan ke activity berikutnya (misal MainActivity)
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Menutup RegisterActivity setelah pindah ke MainActivity

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            String responseBody = null;
                            try {
                                responseBody = new String(error.networkResponse.data, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Log.e("Volley Error", "Status Code: " + statusCode);
                            Log.e("Volley Error", "Response Body: " + responseBody);

                            try {
                                if (responseBody != null) {
                                    JSONObject data = new JSONObject(responseBody);
                                    String errorMessage = data.getString("message");
                                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("Volley Error", "Error: " + error.getMessage());
                            Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}