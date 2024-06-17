package com.example.tanialtech.article.data;

public class MoreArticle {
    private final int imageResId;
    private final String title;
    private final String desc;
    private final String date;

    public MoreArticle(int imageResId, String title, String desc, String date) {
        this.imageResId = imageResId;
        this.title = title;
        this.desc = desc;
        this.date = date;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getDate() {
        return date;
    }
}
