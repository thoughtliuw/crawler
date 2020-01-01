package com.github.thoughtliuw;

import com.github.thoughtliuw.entity.News;
import com.github.thoughtliuw.mybatisDao.MockDataMapper;
import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchDataGenerator {
    public static void main(String[] args) {
        String resource = "db/mybatis/mybatis-config.xml";
        MockDataMapper mapper;
        try (InputStream inputStream = Resources.getResourceAsStream(resource);
        ) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession session = sqlSessionFactory.openSession();
            mapper = session.getMapper(MockDataMapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<News> newsList = mapper.selectNewsList();
        elasticsearchPutData(newsList);
    }

    private static void elasticsearchPutData(List<News> newsList) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")))
        ) {
            int newsListSize = newsList.size();
            BulkRequest bulkRequest = new BulkRequest();
            for (int i = 0; i < newsListSize; i++) {
                News news = newsList.get(i);
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("title", news.getTitle());
                String content = news.getContent();
                jsonMap.put("content", content.length() > 10 ? content.substring(0, 10) : content);
                jsonMap.put("url", news.getUrl());
                jsonMap.put("create_at", news.getCreateAt());
                jsonMap.put("update_at", news.getUpdateAt());
                IndexRequest indexRequest = new IndexRequest("news").source(jsonMap, XContentType.JSON);

                bulkRequest.add(indexRequest);
                System.out.println(indexRequest.toString());
            }
            BulkResponse bulkRes = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            System.out.println(bulkRes.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
