package com.github.thoughtliuw.mybatisDao;

import com.github.thoughtliuw.entity.News;
import org.apache.ibatis.annotations.Param;

public interface CrawlerMapper {
    void insertLinksPool(@Param("url") String url, @Param("tableName") String tableName);

    int countLink(String url);

    void insertNews(News news);

    String getNextLink();

    void deleteLink(String url);
}
