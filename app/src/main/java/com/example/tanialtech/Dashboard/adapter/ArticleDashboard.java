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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ArticleDashboard extends RecyclerView.Adapter<ArticleDashboard.ArticleViewHolder>{

    private Context context;
    private List<JSONObject> articleList;

    public ArticleDashboard(Context context, List<JSONObject> articleList) {
        this.context = context;
        this.articleList = articleList;
    }


    @NonNull
    @Override
    public ArticleDashboard.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_card, parent, false);
        return new ArticleDashboard.ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleDashboard.ArticleViewHolder holder, int position) {
        JSONObject article = articleList.get(position);
        try {
            String title = article.getString("title");
            String desc = article.getString("content");
            String date = article.getString("date");
            String formattedDate = convertToDate(date);

            String imgUrl = "https://api-simdoks.simdoks.web.id/" + article.getString("img_url");

            Log.d("URL:", imgUrl);
            holder.articleTitle.setText(title);
            holder.articleDesc.setText(desc);
            holder.articleDate.setText(formattedDate);
            Glide.with(context).load(imgUrl).apply(new RequestOptions().override(200, 200).centerCrop()).into(holder.articleImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView articleTitle;
        TextView articleDate;
        TextView articleDesc;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.article_image);
            articleTitle = itemView.findViewById(R.id.article_title);
            articleDate = itemView.findViewById(R.id.article_date);
            articleDesc = itemView.findViewById(R.id.article_desc);
        }
    }

    private String convertToDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        String formattedDate = "";
        try {
            date = inputFormat.parse(dateString);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
