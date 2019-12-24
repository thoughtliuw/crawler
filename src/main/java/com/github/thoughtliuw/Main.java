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
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.impl.client.HttpClients.createDefault;

public class Main {
    public static void main(String[] args) {

        List<String> links = new ArrayList<>();
        List<String> parsedLinks = new ArrayList<>();

        CloseableHttpClient httpclient = createDefault();
        links.add("sina.cn");
        while (!links.isEmpty()) {
            String targetUrl = links.remove(links.size() - 1);
            targetUrl = removeBackslashInUrl(targetUrl);

            // 查看这个URL是否已经被处理过
            if (checkIfUrlIsParsed(parsedLinks, targetUrl)) {
                continue;
            }

            // 查看这个URL是否是我们想要的新闻页面
            if (isUsefulUrl(targetUrl)) {

                Document document = parseUrlIfUseful(httpclient,targetUrl);

                List<Element> elements = document.getAllElements();

                putLinkIntoLinks(links, elements);

                String title = getNewsData(elements);

                if(title != null) {
                    System.out.println(title);
                }
            }

            parsedLinks.add(targetUrl);
        }
    }

    private static boolean checkIfUrlIsParsed(List<String> parsedLinks, String targetUrl) {
        return parsedLinks.contains(targetUrl);
    }

    private static String removeBackslashInUrl(String targetUrl) {
        if (targetUrl.contains("\\")) {
            targetUrl = targetUrl.replace("\\", "");
        }
        return targetUrl;
    }

    private static String getNewsData(List<Element> elements) {
        String title = null;
        Element rootElement = elements.get(0);
        Element articleElement = rootElement.selectFirst("article");
        if (articleElement != null) {
            title = articleElement.text();
        }
        return title;
    }

    private static void putLinkIntoLinks(List<String> links, List<Element> elements) {
        elements.forEach(element -> {
            // 把新的a标签都放进链接池中
            if (element.tagName().equals("a")) {
                links.add(element.attr("href"));
            }
        });
    }

    private static Document parseUrlIfUseful(CloseableHttpClient httpclient, String targetUrl) {
        if (!targetUrl.contains("http")) {
            targetUrl = "http://" + targetUrl;
        }
        HttpGet httpGet = new HttpGet(targetUrl);
        String targetHtml = null;
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(targetUrl);
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            targetHtml = EntityUtils.toString(entity1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = Jsoup.parse(targetHtml);
        return document;
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
