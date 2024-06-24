package com.example.tanialtech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.tanialtech.activity.CalendarUtils.daysInMonthArray;
import static com.example.tanialtech.activity.CalendarUtils.monthYearFromDate;
import static com.example.tanialtech.activity.CalendarUtils.daysInWeekArray;

import com.example.tanialtech.R;

public class ActivityFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private Button previousWeekButton;
    private Button nextWeekButton;
    private Button newEventButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        initWidgets(view);
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        setEventAdapter();
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        eventListView = view.findViewById(R.id.eventListView);
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
        EventAdapter eventAdapter = new EventAdapter(getContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    public void newEventAction() {
        startActivity(new Intent(getActivity(), EventEditActivity.class));
    }
}
