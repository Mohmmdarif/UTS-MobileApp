package com.example.tanialtech.activity;

import static android.content.ContentValues.TAG;
import static com.example.tanialtech.activity.CalendarUtils.daysInWeekArray;
import static com.example.tanialtech.activity.CalendarUtils.monthYearFromDate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.tanialtech.Dashboard.adapter.TodoDashboard;
import com.example.tanialtech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
//    private ListView eventListView;
    private RecyclerView eventRecyclerView;
    private Button previousWeekButton;
    private Button nextWeekButton;
    TodoDashboard todoDashboard;
    List<JSONObject> todoList;
    private Button newEventButton;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
//        getProfileData();
        initWidgets(view);
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        setEventAdapter();

        return view;
    }



    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
//        eventListView = view.findViewById(R.id.eventListView);
        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        previousWeekButton = view.findViewById(R.id.previousWeekButton);
        nextWeekButton = view.findViewById(R.id.nextWeekButton);
        newEventButton = view.findViewById(R.id.newEventButton);


        previousWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction();
            }
        });

        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction();
            }
        });

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventAction();
            }
        });


        eventRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        todoList = new ArrayList<>();
        todoDashboard = new TodoDashboard(requireContext(), todoList, new TodoDashboard.OnCheckboxClickListener() {
            @Override
            public void onCheckboxClick(JSONObject todo, boolean isChecked) {
                updateStatusTodo(todo, isChecked);
            }
        });
//        todoDashboard = new TodoDashboard(requireContext(),todoList);
        eventRecyclerView.setAdapter(todoDashboard);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInWeek = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    public void previousMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setMonthView();
        setEventAdapter();
    }

    public void nextMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setMonthView();
        setEventAdapter();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
            setEventAdapter();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateTodo = sdf.format(java.sql.Date.valueOf(String.valueOf(CalendarUtils.selectedDate)));
        fetchTodoData(dateTodo);
        // Update your todoList with dailyEvents
        todoList.clear();
        for (Event event : dailyEvents) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", event.getName()); // Replace with actual fields from Event
                jsonObject.put("time", event.getTime()); // Replace with actual fields from Event
                jsonObject.put("status", false); // Replace with actual fields from Event
            } catch (JSONException e) {
                e.printStackTrace();
            }
            todoList.add(jsonObject);
        }
        eventRecyclerView.getAdapter().notifyDataSetChanged();
//        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
//        EventAdapter eventAdapter = new EventAdapter(getContext(), dailyEvents);
//        eventListView.setAdapter(eventAdapter);
    }

    public void newEventAction() {
        startActivity(new Intent(getActivity(), EventEditActivity.class));
    }

    private void fetchTodoData(String dateTodo) {
        int userId = sharedPreferences.getInt("user_id", -1);
        String accessToken = sharedPreferences.getString("accessToken", "");
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
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String dateTodo = sdf.format(java.sql.Date.valueOf(String.valueOf(CalendarUtils.selectedDate)));
                            fetchTodoData(dateTodo); // Refresh the todo list with the selected date
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


}
