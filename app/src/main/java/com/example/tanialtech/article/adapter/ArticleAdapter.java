package com.example.tanialtech.article.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tanialtech.R;
import com.example.tanialtech.article.data.ArticleItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
    private List<ArticleItem> articleList;
    private OnItemClickListener onItemClickListener;

    public ArticleAdapter(List<ArticleItem> articleList) {
        this.articleList = articleList;
    }

    public interface OnItemClickListener {
        void onItemClick(ArticleItem article);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateData(List<ArticleItem> newArticleList) {
        articleList.clear();
        articleList.addAll(newArticleList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleItem article = articleList.get(position);
        holder.articleTitle.setText(article.getTitle());
        holder.articleDate.setText(formatDate(article.getDate()));

        Glide.with(holder.articleImage.getContext())
                .load("https://api-simdoks.simdoks.web.id/" + article.getImageResId())
                .placeholder(R.drawable.pic_1)
                .error(R.drawable.pic_1)
                .into(holder.articleImage);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(article);
            }
        });
    }

    public void filter(String text) {
        ArrayList<ArticleItem> filteredList = new ArrayList<>();
        for (ArticleItem item : articleList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        articleList = filteredList;
        notifyDataSetChanged();
    }

    private String formatDate(String inputDateString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        try {
            Date date = inputDateFormat.parse(inputDateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDateString; // Return the original string in case of error
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        public ImageView articleImage;
        public TextView articleTitle;
        public TextView articleDate;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.card_image);
            articleTitle = itemView.findViewById(R.id.article_title);
            articleDate = itemView.findViewById(R.id.article_date);
        }
    }

}
