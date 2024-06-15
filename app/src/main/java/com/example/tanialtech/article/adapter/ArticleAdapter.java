package com.example.tanialtech.article.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tanialtech.R;
import com.example.tanialtech.article.data.ArticleItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        holder.articleImage.setImageResource(article.getImageResId());
        holder.articleTitle.setText(article.getTitle());
        holder.articleDate.setText(article.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(article);
                }
            }

        });
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
