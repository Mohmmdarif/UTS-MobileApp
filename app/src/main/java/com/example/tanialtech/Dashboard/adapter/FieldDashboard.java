package com.example.tanialtech.Dashboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tanialtech.R;

import org.json.JSONObject;

import java.util.List;

public class FieldDashboard extends RecyclerView.Adapter<FieldDashboard.FieldViewHolder>{
    private Context context;
    private List<JSONObject> fieldList;

    public FieldDashboard(Context context, List<JSONObject> fieldList) {
        this.context = context;
        this.fieldList = fieldList;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.field_card, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        JSONObject field = fieldList.get(position);
        try {
            String name = field.getString("name");
            String code = field.getString("code");
            String imgUrl = "https://api-simdoks.simdoks.web.id/" + field.getString("img_url");

            Log.d("URL:", imgUrl);
            holder.fieldTitle.setText(name);
            holder.fieldCode.setText(code);
            Glide.with(context).load(imgUrl).apply(new RequestOptions().override(350, 350).centerCrop()).into(holder.fieldImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView fieldImage;
        TextView fieldTitle;
        TextView fieldCode;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldImage = itemView.findViewById(R.id.card_image);
            fieldTitle = itemView.findViewById(R.id.card_title);
            fieldCode = itemView.findViewById(R.id.card_code);
        }
    }
}
