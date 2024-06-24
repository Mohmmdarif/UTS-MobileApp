package com.example.tanialtech.article.data;

public class MoreArticle {
    private int id;
    private String imageResId;
    private String title;
    private String desc;
    private String date;

    public MoreArticle(int id, String imageResId, String title, String desc, String date) {
        this.imageResId = imageResId;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageResId() {
        return imageResId;
    }

    public void setImageResId(String imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
