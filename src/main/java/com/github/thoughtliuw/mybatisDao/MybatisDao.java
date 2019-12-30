package com.github.thoughtliuw.mybatisDao;

import com.github.thoughtliuw.Dao;
import com.github.thoughtliuw.entity.News;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class MybatisDao implements Dao {
    private static final String resource = "db/mybatis/mybatis-config.xml";
    private final InputStream inputStream;
    private SqlSessionFactory sqlSessionFactory;

    public MybatisDao() {
        try {
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertLinksTobeProcessed(String url) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            CrawlerMapper mapper = session.getMapper(CrawlerMapper.class);
            mapper.insertLinksPool(url, "LINKS_TO_BE_PROCESSED");
        }
    }

    @Override
    public void insertLinksAlreadyProcessed(String url) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            CrawlerMapper mapper = session.getMapper(CrawlerMapper.class);
            mapper.insertLinksPool(url, "LINKS_ALREADY_PROCESSED");
        }
    }

    @Override
    public boolean checkIfUrlIsParsed(String url) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CrawlerMapper mapper = session.getMapper(CrawlerMapper.class);
            mapper.insertLinksPool(url, "LINKS_ALREADY_PROCESSED");
        }
        return false;
    }

    @Override
    public synchronized String getNextLinkAndDelete() throws SQLException {
        String url;
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            CrawlerMapper mapper = session.getMapper(CrawlerMapper.class);
            url = mapper.getNextLink();
            if (url != null) {
                mapper.deleteLink(url);
            }
        }
        return url;
    }

    @Override
    public void storeNewsIntoDataBase(String url, String title, String content) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            CrawlerMapper mapper = session.getMapper(CrawlerMapper.class);
            mapper.insertNews(new News(url, title, content));
        }
    }
}
