package com.example.tanialtech.article.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanialtech.R;
import com.example.tanialtech.article.data.MoreArticle;

import java.util.List;

public class MoreArticleAdapter extends RecyclerView.Adapter<MoreArticleAdapter.ArticleViewHolder> {

    private List<MoreArticle> moreArticleList;
    private OnItemClickListener onItemClickListener;

    public MoreArticleAdapter(List<MoreArticle> moreArticleList) {
        this.moreArticleList = moreArticleList;
    }

    public interface OnItemClickListener {
        void onItemClick(MoreArticle article1);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_article_item, parent, false);
        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        MoreArticle article = moreArticleList.get(position);
        holder.moreArticleImage.setImageResource(article.getImageResId());
        holder.moreArticleTitle.setText(article.getTitle());
        holder.moreArticleDesc.setText(article.getDesc());
        holder.moreArticleDate.setText(article.getDate());

        holder.itemView.setOnClickListener(v -> {
            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moreArticleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        public ImageView moreArticleImage;
        public TextView moreArticleTitle;
        public TextView moreArticleDesc;
        public TextView moreArticleDate;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            moreArticleImage = itemView.findViewById(R.id.more_article_image);
            moreArticleTitle = itemView.findViewById(R.id.more_article_title);
            moreArticleDesc = itemView.findViewById(R.id.more_article_desc);
            moreArticleDate = itemView.findViewById(R.id.more_article_date);
        }
    }
}
