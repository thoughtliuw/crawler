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
            if (targetUrl.contains("\\")) {
                targetUrl = targetUrl.replace("\\", "");
            }

            // 查看这个URL是否已经被处理过
            if (parsedLinks.contains(targetUrl)) {
                continue;
            }

            // 查看这个URL是否是我们想要的新闻页面
            if (targetUrl.contains("news.sina.cn")
                    || targetUrl.equals("sina.cn")) {
                // 访问链接并解析
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
                List<Element> elements = document.getAllElements();

                elements.forEach(element -> {
                    // 把新的a标签都放进链接池中
                    if (element.tagName().equals("a")) {
                        links.add(element.attr("href"));
                    }
                });

                Element rootElement = elements.get(0);
                Element articleElement = rootElement.selectFirst("article");
                if (articleElement != null) {
                    String title = articleElement.text();
                    System.out.println(title);
                }
            }

            parsedLinks.add(targetUrl);
        }
    }
}
