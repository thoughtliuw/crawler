package com.github.thoughtliuw;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.impl.client.HttpClients.createDefault;

public class Crawler implements Runnable {

    private Dao dao;

    public Crawler(Dao dao) {
        this.dao = dao;
    }

    @Override
    public void run() {
        try {
            CloseableHttpClient httpclient = createDefault();

            String targetUrl;
            while ((targetUrl = dao.getNextLinkAndDelete()) != null) {

                // 查询数据库中是否已经处理过这条数据
                if (dao.checkIfUrlIsParsed(targetUrl)) {
                    continue;
                }

                // 查看这个URL是否是我们想要的新闻页面
                if (isUsefulUrl(targetUrl)) {

                    Document document = getAndParseUrl(httpclient, targetUrl);

                    // 把文档中的a标签的href都存入数据库
                    parseLinksAndStoreIntoDBLinks(document);

                    //如果是新闻页面就存入数据库的新闻表中，否则就什么都不做
                    storeIntoDatabaseItIsNewsPage(targetUrl, document);

                }

                // 向数据库已处理链接池中插入数据
                dao.insertLinksAlreadyProcessed(targetUrl);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void storeIntoDatabaseItIsNewsPage(String url, Document document) throws SQLException {
        Element rootElement = document.getAllElements().get(0);
        Element articleElement = rootElement.selectFirst("article");
        if (articleElement != null) {
            String title = articleElement.selectFirst("h1").text();
            ArrayList<Element> contentEls = articleElement.selectFirst(".art_content").select(".art_p");
            String content = contentEls.stream().map(Element::text).collect(Collectors.joining("\n"));
            dao.storeNewsIntoDataBase(url, title, content);
        }
    }

    private String removeBackslashInUrl(String targetUrl) {
        if (targetUrl.contains("\\")) {
            targetUrl = targetUrl.replace("\\", "");
        }
        return targetUrl;
    }

    private Document getAndParseUrl(CloseableHttpClient httpclient, String targetUrl) throws IOException {
        HttpGet httpGet = new HttpGet(targetUrl);
        String targetHtml;
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(targetUrl);
            HttpEntity entity1 = response1.getEntity();
            targetHtml = EntityUtils.toString(entity1);
        }
        return Jsoup.parse(targetHtml);
    }

    private boolean isUsefulUrl(String targetUrl) {
        return isNewsPage(targetUrl)
                || isInitPage(targetUrl);
    }

    private boolean isInitPage(String targetUrl) {
        return targetUrl.equals("https://sina.cn");
    }

    private boolean isNewsPage(String targetUrl) {
        return targetUrl.contains("news.sina.cn");
    }

    public void parseLinksAndStoreIntoDBLinks(Document document) throws SQLException {
        List<Element> links = document.select("a");

        // 将链接放入待处理数据库链接池中
        for (Element link : links) {

            String href = link.attr("href");
            String url = href;
            if (url.startsWith("//")) {
                url = "https:" + href;
            }
            url = removeBackslashInUrl(url);
            if (!href.toLowerCase().startsWith("javascript")) {
                dao.insertLinksTobeProcessed(url);
            }
        }
    }
}
