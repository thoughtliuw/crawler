package com.github.thoughtliuw;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Scanner;

public class ElasticSearchEngine {

    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入查询关键字：");
        String keyword = scanner.nextLine();
        search(keyword);
    }

    private static void search(String keyword) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")))
        ) {
            SearchRequest searchRequest = new SearchRequest("news");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("content", keyword);
            searchSourceBuilder.query(matchQueryBuilder);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            searchResponse.getHits().forEach(hit -> System.out.println(hit.getSourceAsString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
