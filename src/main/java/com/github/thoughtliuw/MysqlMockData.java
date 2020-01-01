package com.github.thoughtliuw;

import com.github.thoughtliuw.entity.News;
import com.github.thoughtliuw.mybatisDao.MockDataMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class MysqlMockData {
    private static void mockData(SqlSessionFactory sqlSessionFactory, int targetNum) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            MockDataMapper mapper = session.getMapper(MockDataMapper.class);
            List<News> newsList = mapper.selectNewsList();

            int size = newsList.size();
            int secondsOfYear = 3600 * 24 * 365;
            int count = targetNum - size;
            Random random = new Random();
            try {
                while (count-- > 0) {
                    int index = random.nextInt(size);
                    News oldNews = newsList.get(index);
                    News newsTobeInserted = new News(oldNews);
                    Instant created_at = newsTobeInserted.getCreateAt();
                    created_at = created_at.minusSeconds(random.nextInt(secondsOfYear));
                    newsTobeInserted.setCreateAt(created_at);
                    mapper.insertNews(newsTobeInserted);
                    if (count % 2000 == 0) {
                        session.flushStatements();
                        System.out.println(count);
                    }
                }
                session.commit();
            } catch (Exception e) {
                session.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        String resource = "db/mybatis/mybatis-config.xml";
        InputStream inputStream;
        SqlSessionFactory sqlSessionFactory;
        try {
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            mockData(sqlSessionFactory, 100_000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
