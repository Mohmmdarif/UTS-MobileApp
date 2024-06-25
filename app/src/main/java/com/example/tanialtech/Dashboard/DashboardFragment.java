package com.example.tanialtech.Dashboard;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanialtech.Dashboard.adapter.ArticleDashboard;
import com.example.tanialtech.Dashboard.adapter.FieldDashboard;
import com.example.tanialtech.Dashboard.adapter.TodoDashboard;
import com.example.tanialtech.R;
import com.example.tanialtech.activity.ActivityFragment;
import com.example.tanialtech.article.ArticleFragment;
import com.example.tanialtech.field.FieldFragment;
import com.example.tanialtech.profile.ProfileFragmentDashboard;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DashboardFragment extends Fragment {

    ConstraintLayout section4;

    RecyclerView recyclerViewLadang, recyclerViewArticle, recyclerViewTodo;
    FieldDashboard fieldDashboard;
    ArticleDashboard articleDashboard;
    TodoDashboard todoDashboard;
    List<JSONObject> fieldList, articleList, todoList;

    TextView temperature, status_kelembapan, status_angin, status_tekanan, location, date,cuaca, username;
    TextView noTodoData, noFieldData, noArticleData, todoFragment, fieldFragment, articleFragment;
    ImageView profile;
    private static final String API_KEY = "135aacaf967d8fb659e544fc70ee92c9"; // Ganti dengan API key Anda
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    public DashboardFragment() {

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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        CardView cardView = view.findViewById(R.id.card);

        recyclerViewLadang = view.findViewById(R.id.recyclerViewLadang);
        recyclerViewLadang.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        fieldList = new ArrayList<>();
        fieldDashboard = new FieldDashboard(requireContext(), fieldList);
        recyclerViewLadang.setAdapter(fieldDashboard);

        recyclerViewArticle = view.findViewById(R.id.recyclerViewArticle);
        recyclerViewArticle.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        articleList = new ArrayList<>();
        articleDashboard = new ArticleDashboard(requireContext(), articleList);
        recyclerViewArticle.setAdapter(articleDashboard);

        recyclerViewTodo = view.findViewById(R.id.recyclerViewTodo);
        recyclerViewTodo.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        todoList = new ArrayList<>();
        todoDashboard = new TodoDashboard(requireContext(), todoList, new TodoDashboard.OnCheckboxClickListener() {
            @Override
            public void onCheckboxClick(JSONObject todo, boolean isChecked) {
                updateStatusTodo(todo, isChecked);
            }
        });
//        todoDashboard = new TodoDashboard(requireContext(),todoList);
        recyclerViewTodo.setAdapter(todoDashboard);

        todoFragment = view.findViewById(R.id.todoFragment);
        fieldFragment = view.findViewById(R.id.fieldFragment);
        articleFragment = view.findViewById(R.id.articleFragment);
        username = view.findViewById(R.id.username);
        section4 = view.findViewById(R.id.section4);
        temperature = view.findViewById(R.id.temperature);
        status_kelembapan = view.findViewById(R.id.status_kelembapan);
        status_angin = view.findViewById(R.id.status_angin);
        status_tekanan = view.findViewById(R.id.status_tekanan);
        location = view.findViewById(R.id.location);
        date = view.findViewById(R.id.date);
        cuaca = view.findViewById(R.id.deskripsiCuaca);
        profile = view.findViewById(R.id.profile_icon);
        noArticleData = view.findViewById(R.id.noArticleData);
        noFieldData = view.findViewById(R.id.noFieldData);
        noTodoData = view.findViewById(R.id.noTodoData);

        getProfileData();
        fetchTodoData();
        fetchFieldData();
        fetchArticleData();

//        todoDashboard = new TodoDashboard(requireContext(), todoList, new TodoDashboard.OnCheckboxClickListener() {
//            @Override
//            public void onCheckboxClick(JSONObject todo, boolean isChecked) {
//                updateStatusTodo(todo, isChecked);
//            }
//        });
        todoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTodoFragment();
            }
        });

        fieldFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFieldFragment();
            }
        });

        articleFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayArticleFragment();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProfileFragment();
            }
        });

        setDate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }

        // Set up location callback for continuous location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Dapatkan koordinat lokasi
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d(TAG, "Location updated: " + latitude + ", " + longitude);
                        getCityName(latitude, longitude);
                    }
                }
            }
        };
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayWeatherFragment();
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTodoData(); // Refresh the todo data every time the fragment resumes
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
                        Log.e("API Error", "Response Data: " + new String(error.networkResponse.data));
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

    private void fetchFieldData() {
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");
        String url = "https://api-simdoks.simdoks.web.id/fields/user/" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject field = response.getJSONObject(i);
                                fieldList.add(field);
                            }
                            if (fieldList.isEmpty()) {
                                noFieldData.setVisibility(View.VISIBLE);
                            } else {
                                noFieldData .setVisibility(View.GONE);
                            }
                            fieldDashboard.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.getMessage());
                        Toast.makeText(getActivity(), "Failed to get field data.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken); // Menggunakan accessToken untuk otorisasi
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchArticleData() {
//        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");
        String url = "https://api-simdoks.simdoks.web.id/articles";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject article = response.getJSONObject(i);
                                articleList.add(article);
                            }
                            if (articleList.isEmpty()) {
                                noArticleData.setVisibility(View.VISIBLE);
                            } else {
                                noArticleData.setVisibility(View.GONE);
                            }
                            articleDashboard.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.getMessage());
                        Toast.makeText(getActivity(), "Failed to get article data.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken); // Menggunakan accessToken untuk otorisasi
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchTodoData() {
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateTodo = sdf.format(new Date());
        Log.d("Date:", dateTodo);
        String url = "https://api-simdoks.simdoks.web.id/todos/user/" + userId + "/date/" + dateTodo;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Response: " + response.toString());
                        todoList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject todo = response.getJSONObject(i);
                                todoList.add(todo);
                            }
                            if (todoList.isEmpty()) {
                                noTodoData.setVisibility(View.VISIBLE);
                            } else {
                                noTodoData.setVisibility(View.GONE);
                            }
                            todoDashboard.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.getMessage());
                        Toast.makeText(requireContext(), "Failed to get todo data.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    private void updateStatusTodo(JSONObject todo, boolean isChecked) {
        try {
            int todoId = todo.getInt("id");
            String accessToken = sharedPreferences.getString("accessToken", "");

            String url = "https://api-simdoks.simdoks.web.id/todo/" + todoId;

            JSONObject updateStatus = new JSONObject();
            updateStatus.put("status", isChecked);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, updateStatus,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("TAG", "Todo status updated: " + response.toString());
                            fetchTodoData(); // Refresh the todo list
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error", "Error: " + error.getMessage());
                            Toast.makeText(requireContext(), "Failed to update todo status.", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setDate() {
        Locale indonesianLocale = new Locale("in", "ID");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", indonesianLocale);
        String currentDate = dateFormat.format(new Date());
        date.setText(currentDate);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Dapatkan koordinat lokasi
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d(TAG, "Location obtained: " + latitude + ", " + longitude);
                    getCityName(latitude, longitude);
                } else {
                    // Jika lokasi tidak tersedia, request location updates
                    Log.d(TAG, "Location is null, requesting location updates");
                    requestLocationUpdates();
                }
            }
        }).addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Jika gagal mendapatkan lokasi
                Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to get location: " + e.getMessage());
            }
        });
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void getCityName(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                if (cityName != null) {
                    fetchWeatherData(cityName);
                } else {
                    Log.e(TAG, "City name is null");
                }
            } else {
                Log.e(TAG, "No addresses found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Geocoder IOException: " + e.getMessage());
        }
    }

    private void fetchWeatherData(String cityName) {
        cityName = "Jakarta"; // Temporary hard-coded for testing
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            JSONObject main = response.getJSONObject("main");

                            double temp = main.getDouble("temp") - 273.15; // Konversi dari Kelvin ke Celsius
                            int humidity = main.getInt("humidity");
                            double pressure = main.getDouble("pressure");

                            double pressureKPa = pressure / 10.0;

                            JSONObject wind = response.getJSONObject("wind");
                            double windSpeed = wind.getDouble("speed");
                            String weather = response.getJSONArray("weather").getJSONObject(0).getString("main");

                            String cityName = response.getString("name");

                            // Menampilkan data di TextView
                            temperature.setText(String.format(Locale.getDefault(), "%.2f", temp) + "°C");
                            status_kelembapan.setText(humidity + "%");
                            status_tekanan.setText(String.format(Locale.getDefault(), "%.2f kPa", pressureKPa));
                            status_angin.setText(String.format(Locale.getDefault(), "%.2f", windSpeed) + " m/s");
                            location.setText(cityName + ", Indonesia");
                            cuaca.setText(weather);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.toString());
                        Toast.makeText(requireContext(), "Failed to get weather data.", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required to get weather data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

//    private void displayWeatherFragment() {
//        FragmentManager fragmentManager = getChildFragmentManager();
//
//        Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//        if (previousFragment != null) {
//            fragmentManager.beginTransaction().remove(previousFragment).commitNow();
//        }
//
//        WeatherFragment weatherFragment = new WeatherFragment();
//
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        fragmentTransaction.replace(R.id.fragment_container, weatherFragment);
//        fragmentTransaction.addToBackStack(null);
//
//        fragmentTransaction.commit();
//
//        View previousView = getView();
//        if (previousView != null) {
//            CardView cardView = previousView.findViewById(R.id.card);
//            if (cardView != null) {
//                ViewGroup parentView = (ViewGroup) cardView.getParent();
//                parentView.removeView(cardView);
//            }
//        }
//    }

        private void displayProfileFragment(){
            ConstraintLayout constraintLayout = requireView().findViewById(R.id.fragment_container);
            constraintLayout.removeAllViews();
            ProfileFragmentDashboard profileFragmentDashboard = new ProfileFragmentDashboard();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(constraintLayout.getId(), profileFragmentDashboard)
                    .addToBackStack(null) // Ini untuk menambahkan ke back stack
                    .commit();

            // Sembunyikan komponen Dashboard yang lainnya jika diperlukan
            hideDashboardComponents();

//            FragmentManager fragmentManager = getChildFragmentManager();
//
//            Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//            if (previousFragment != null) {
//                fragmentManager.beginTransaction().remove(previousFragment).commitNow();
//            }
//
//            ProfileFragment profileFragment = new ProfileFragment();
//
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            fragmentTransaction.replace(R.id.fragment_container, profileFragment);
//            fragmentTransaction.addToBackStack(null);
//            hideDashboardComponents();
//
//
//            fragmentTransaction.commit();
//
//            View previousView = getView();
//            if (previousView != null) {
//                CardView cardView = previousView.findViewById(R.id.card);
//                if (cardView != null) {
//                    ViewGroup parentView = (ViewGroup) cardView.getParent();
//                    parentView.removeView(cardView);
//                }
//            }


        }

        private void displayTodoFragment(){
            ConstraintLayout constraintLayout = requireView().findViewById(R.id.fragment_container);

            // Hapus semua tampilan dari ConstraintLayout
            constraintLayout.removeAllViews();

            // Buat instance dari FieldFragment
            ActivityFragment activityFragment = new ActivityFragment();

            // Dapatkan FragmentManager
            FragmentManager fragmentManager = getChildFragmentManager();

            // Lakukan transaksi fragment
            fragmentManager.beginTransaction()
                    .replace(constraintLayout.getId(), activityFragment)
                    .addToBackStack(null) // Ini untuk menambahkan ke back stack
                    .commit();

            // Sembunyikan komponen Dashboard yang lainnya jika diperlukan
            hideDashboardComponents();
//            FragmentManager fragmentManager = getChildFragmentManager();
//
//            Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//            if (previousFragment != null) {
//                fragmentManager.beginTransaction().remove(previousFragment).commitNow();
//            }
//
//            ActivityFragment activityFragment = new ActivityFragment();
//
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            fragmentTransaction.replace(R.id.fragment_container, activityFragment);
//            fragmentTransaction.addToBackStack(null);
////            hideDashboardComponents();
//
//
//            fragmentTransaction.commit();
//
//            View previousView = getView();
//            if (previousView != null) {
//                CardView cardView = previousView.findViewById(R.id.card);
//                if (cardView != null) {
//                    ViewGroup parentView = (ViewGroup) cardView.getParent();
//                    parentView.removeView(cardView);
//                }
//            }
        }

        private void displayFieldFragment(){
            ConstraintLayout constraintLayout = requireView().findViewById(R.id.fragment_container);

            // Hapus semua tampilan dari ConstraintLayout
            constraintLayout.removeAllViews();

            // Buat instance dari FieldFragment
            FieldFragment fieldFragment = new FieldFragment();

            // Dapatkan FragmentManager
            FragmentManager fragmentManager = getChildFragmentManager();

            // Lakukan transaksi fragment
            fragmentManager.beginTransaction()
                    .replace(constraintLayout.getId(), fieldFragment)
                    .addToBackStack(null) // Ini untuk menambahkan ke back stack
                    .commit();

            // Sembunyikan komponen Dashboard yang lainnya jika diperlukan
            hideDashboardComponents();
//            FragmentManager fragmentManager = getChildFragmentManager();
//
//            Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//            if (previousFragment != null) {
//                fragmentManager.beginTransaction().remove(previousFragment).commitNow();
//            }
//
//            FieldFragment fieldFragment1 = new FieldFragment();
//
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            fragmentTransaction.replace(R.id.fragment_container, fieldFragment1);
//            fragmentTransaction.addToBackStack(null);
//            hideDashboardComponents();
//
//
//            fragmentTransaction.commit();
//
//            View previousView = getView();
//            if (previousView != null) {
//                CardView cardView = previousView.findViewById(R.id.card);
//                if (cardView != null) {
//                    ViewGroup parentView = (ViewGroup) cardView.getParent();
//                    parentView.removeView(cardView);
//                }
//            }
        }



    private void displayArticleFragment(){
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.fragment_container);

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

        // Sembunyikan komponen Dashboard yang lainnya jika diperlukan
        hideDashboardComponents();
//            FragmentManager fragmentManager = getChildFragmentManager();
//
//            Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//            if (previousFragment != null) {
//                fragmentManager.beginTransaction().remove(previousFragment).commitNow();
//            }
//
//            ArticleFragment articleFragment1 = new ArticleFragment();
//
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            fragmentTransaction.replace(R.id.fragment_container, articleFragment1);
//            fragmentTransaction.addToBackStack(null);
////            hideDashboardComponents();
//
//
//            fragmentTransaction.commit();
//
//            View previousView = getView();
//            if (previousView != null) {
//                CardView cardView = previousView.findViewById(R.id.card);
//                if (cardView != null) {
//                    ViewGroup parentView = (ViewGroup) cardView.getParent();
//                    parentView.removeView(cardView);
//                }
//            }
        }

    private void hideDashboardComponents() {
        section4.setVisibility(View.GONE);
    }
}