package com.example.tanialtech.Dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanialtech.R;

import org.json.JSONObject;

import java.util.List;

public class TodoDashboard extends RecyclerView.Adapter<TodoDashboard.TodoViewHolder>{
    private Context context;
    private List<JSONObject> todoList;

    private OnCheckboxClickListener onCheckboxClickListener;

    public interface OnCheckboxClickListener {
        void onCheckboxClick(JSONObject todo, boolean isChecked);
    }

    public TodoDashboard(Context context, List<JSONObject> todoList, OnCheckboxClickListener onCheckboxClickListener) {
        this.context = context;
        this.todoList = todoList;
        this.onCheckboxClickListener = onCheckboxClickListener;
    }
//    public TodoDashboard(Context context, List<JSONObject> todoList) {
//        this.context = context;
//        this.todoList = todoList;
//    }


    @NonNull
    @Override
    public TodoDashboard.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_card, parent, false);
        return new TodoDashboard.TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoDashboard.TodoViewHolder holder, int position) {
        JSONObject todo = todoList.get(position);
        try {
            String title = todo.getString("name");
            String time = todo.getString("time");
            Boolean status = todo.getBoolean("status");

            holder.todoTitle.setText(title);
            holder.todoTime.setText(time);
            holder.checklistBox.setChecked(status);

            holder.checklistBox.setOnCheckedChangeListener(null); // Clear previous listener
            holder.checklistBox.setChecked(status);

            holder.checklistBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (onCheckboxClickListener != null) {
                    onCheckboxClickListener.onCheckboxClick(todo, isChecked);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView todoTitle;
        TextView todoTime;
        CheckBox checklistBox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.title_todo);
            todoTime = itemView.findViewById(R.id.textView2);
            checklistBox = itemView.findViewById(R.id.checklistBox);
        }
    }
}
