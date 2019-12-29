package com.github.thoughtliuw.entity;

public class News {
    private String title;
    private String url;
    private String content;
    private String id;
    public News(String url, String title, String content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
