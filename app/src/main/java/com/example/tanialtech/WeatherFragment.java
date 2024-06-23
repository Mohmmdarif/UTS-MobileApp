package com.example.tanialtech;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeatherFragment extends Fragment {

    TextView temperature, status_kelembapan, status_angin, status_tekanan, location, date,cuaca;
    private static final String API_KEY = "135aacaf967d8fb659e544fc70ee92c9"; // Ganti dengan API key Anda
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        temperature = view.findViewById(R.id.temperature_1);
        status_kelembapan = view.findViewById(R.id.status_kelembapan);
        status_angin = view.findViewById(R.id.status_angin);
        status_tekanan = view.findViewById(R.id.status_tekanan);
        location = view.findViewById(R.id.location);
        date = view.findViewById(R.id.date);
        cuaca = view.findViewById(R.id.deskripsiCuaca);

        // Set current date
        setDate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
        return view;
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
                            weather = translateWeatherDescription(weather);

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

    private String translateWeatherDescription(String description) {
        Map<String, String> weatherDescriptions = new HashMap<>();
        weatherDescriptions.put("clear sky", "cerah");
        weatherDescriptions.put("few clouds", "berawan sebagian");
        weatherDescriptions.put("scattered clouds", "berawan tersebar");
        weatherDescriptions.put("broken clouds", "berawan tebal");
        weatherDescriptions.put("overcast clouds", "berawan");
        weatherDescriptions.put("mist", "berkabut");
        weatherDescriptions.put("light intensity drizzle", "gerimis ringan");
        weatherDescriptions.put("drizzle", "gerimis");
        weatherDescriptions.put("heavy intensity drizzle", "gerimis lebat");
        weatherDescriptions.put("light intensity shower rain", "hujan gerimis ringan");
        weatherDescriptions.put("shower rain", "hujan gerimis");
        weatherDescriptions.put("heavy intensity shower rain", "hujan lebat");
        weatherDescriptions.put("light rain", "hujan ringan");
        weatherDescriptions.put("moderate rain", "hujan sedang");
        weatherDescriptions.put("heavy intensity rain", "hujan lebat");
        weatherDescriptions.put("very heavy rain", "hujan sangat lebat");
        weatherDescriptions.put("extreme rain", "hujan ekstrem");
        weatherDescriptions.put("freezing rain", "hujan beku");
        weatherDescriptions.put("light snow", "salju ringan");
        weatherDescriptions.put("snow", "salju");
        weatherDescriptions.put("heavy snow", "salju lebat");
        weatherDescriptions.put("sleet", "hujan salju");
        weatherDescriptions.put("shower sleet", "hujan salju lebat");
        weatherDescriptions.put("light shower sleet", "hujan salju ringan");
        weatherDescriptions.put("fog", "kabut");
        weatherDescriptions.put("sand/dust whirls", "puting beliung pasir/debu");
        weatherDescriptions.put("sand", "pasir");
        weatherDescriptions.put("dust", "debu");
        weatherDescriptions.put("volcanic ash", "abu vulkanik");
        weatherDescriptions.put("squalls", "puting beliung");
        weatherDescriptions.put("tornado", "puting beliung");
        weatherDescriptions.put("clear sky", "cerah");
        weatherDescriptions.put("few clouds", "berawan sebagian");
        weatherDescriptions.put("scattered clouds", "berawan tersebar");
        weatherDescriptions.put("broken clouds", "berawan tebal");
        weatherDescriptions.put("shower rain", "hujan ringan");
        weatherDescriptions.put("rain", "hujan");

        // Cek apakah deskripsi cuaca ada dalam hashmap
        if (weatherDescriptions.containsKey(description)) {
            return weatherDescriptions.get(description);
        } else {
            // Jika tidak ditemukan dalam hashmap, kembalikan deskripsi asli
            return description;
        }
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
}
