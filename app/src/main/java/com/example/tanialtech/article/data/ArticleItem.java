package com.example.tanialtech.article.data;

public class ArticleItem {
    private int id;
    private String imageResId;
    private String title;
    private String date;

    public ArticleItem(int id, String title, String date, String imageResId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.imageResId = imageResId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageResId() {
        return imageResId;
    }

    public void setImageResId(String imageResId) {
        this.imageResId = imageResId;
    }
}
