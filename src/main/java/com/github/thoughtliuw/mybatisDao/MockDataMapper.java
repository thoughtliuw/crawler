package com.github.thoughtliuw.mybatisDao;

import com.github.thoughtliuw.entity.News;

import java.util.List;

public interface MockDataMapper {
    List<News> selectNewsList();

    void insertNews(News news);
}
