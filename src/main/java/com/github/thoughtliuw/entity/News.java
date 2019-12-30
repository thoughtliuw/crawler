package com.github.thoughtliuw.entity;

import java.time.Instant;

public class News {
    private String title;
    private String url;
    private String content;
    private Instant createAt;

    private Instant updateAt;

    public News() {

    }

    public News(String url, String title, String content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public News(News oldNews) {
        this.title = oldNews.title;
        this.url = oldNews.url;
        this.content = oldNews.content;
        this.createAt = oldNews.createAt;
        this.updateAt = oldNews.updateAt;
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


    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }
}
