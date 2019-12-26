package com.github.thoughtliuw;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.impl.client.HttpClients.createDefault;

public class Main {

    private static String getNextLinkAndDelete(Connection connection) throws SQLException {
        String url = getNextLink(connection);
        if (url != null) {
            updateDatabase(connection, url, "delete from LINKS_TO_BE_PROCESSED where link = ?");
        }
        return url;
    }

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public static void main(String[] args) throws SQLException, IOException {
//        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./news", "root", "root");
        CloseableHttpClient httpclient = createDefault();

        String targetUrl;
        while ((targetUrl = getNextLinkAndDelete(connection)) != null) {

            // 查询数据库中是否已经处理过这条数据
            if (checkIfUrlIsParsed(connection, "select * from LINKS_TO_BE_PROCESSED where link = ?")) {
                continue;
            }

            // 查看这个URL是否是我们想要的新闻页面
            if (isUsefulUrl(targetUrl)) {

                Document document = getAndParseUrl(httpclient, targetUrl);

                // 把文档中的a标签的href都存入数据库
                parseUrlsAndStoreIntoDB(connection, document);

                //如果是新闻页面就存入数据库的新闻表中，否则就什么都不做
                storeIntoDatabaseItIsNewsPage(connection, targetUrl, document);

            }

            // 向数据库已处理链接表中插入数据
            updateDatabase(connection, targetUrl, "insert into LINKS_TO_BE_PROCESSED values(?)");
        }
    }

    private static void parseUrlsAndStoreIntoDB(Connection connection, Document document) throws SQLException {
        List<Element> links = document.select("a");

        // 将链接放入待处理数据库链接池中
        for (Element link : links) {
            String href = link.attr("href");
            updateDatabase(connection, href, "insert into LINKS_TO_BE_PROCESSED values(?)");
        }
    }

    private static void updateDatabase(Connection connection, String param1, String s) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(s)) {
            preparedStatement.setString(1, param1);
            preparedStatement.executeUpdate();
        }
    }

    private static String getNextLink(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from LINKS_TO_BE_PROCESSED limit 1");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("link");
            }
        }
        return null;
    }


    private static boolean checkIfUrlIsParsed(Connection connection, String sql) throws SQLException {
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, sql);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return false;
    }

    private static String removeBackslashInUrl(String targetUrl) {
        if (targetUrl.contains("\\")) {
            targetUrl = targetUrl.replace("\\", "");
        }
        return targetUrl;
    }

    private static void storeIntoDatabaseItIsNewsPage(Connection connection, String url, Document document) throws SQLException {
        Element rootElement = document.getAllElements().get(0);
        Element articleElement = rootElement.selectFirst("article");
        if (articleElement != null) {
            String title = articleElement.selectFirst("h1").text();
            ArrayList<Element> contentEls = articleElement.selectFirst(".art_content").select(".art_p");
            String content = contentEls.stream().map(Element::text).collect(Collectors.joining("\n"));
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into news(title,content,url,createAt,updateAt) values (?,?,?,now(),now())")) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, content);
                preparedStatement.setString(3, url);
                preparedStatement.executeUpdate();
            }
        }
    }

    private static Document getAndParseUrl(CloseableHttpClient httpclient, String targetUrl) throws IOException {
        if (!targetUrl.contains("http")) {
            targetUrl = "http://" + targetUrl;
        }

        targetUrl = removeBackslashInUrl(targetUrl);

        HttpGet httpGet = new HttpGet(targetUrl);
        String targetHtml;
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(targetUrl);
            HttpEntity entity1 = response1.getEntity();
            targetHtml = EntityUtils.toString(entity1);
        }
        return Jsoup.parse(targetHtml);
    }

    private static boolean isUsefulUrl(String targetUrl) {
        return isNewsPage(targetUrl)
                || isInitPage(targetUrl);
    }

    private static boolean isInitPage(String targetUrl) {
        return targetUrl.equals("sina.cn");
    }

    private static boolean isNewsPage(String targetUrl) {
        return targetUrl.contains("news.sina.cn");
    }

}
