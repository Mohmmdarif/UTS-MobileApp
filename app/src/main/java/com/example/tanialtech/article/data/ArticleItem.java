package com.example.tanialtech.article.data;

public class ArticleItem {
    private final int imageResId;
    private final String title;
    private final String date;

    public ArticleItem(String title, String date, int imageResId) {
        this.title = title;
        this.date = date;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getImageResId() {
        return imageResId;
    }
}
