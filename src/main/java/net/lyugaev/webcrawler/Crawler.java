package net.lyugaev.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by dmitry on 15.09.16.
 */
public class Crawler {

    private int maxSearchDepth;
    private HashSet<String> crawledLinks = new HashSet<String>();

    public Crawler(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
    }

    public int getcrawledLinksSize() {
        return crawledLinks.size();
    }

    public void init(String startUrl) {
        crawledLinks.clear();
        crawledLinks.add(startUrl);
    }

    public void crawl(String url, int linkDepth) {
        processLink(url, linkDepth);

        if (linkDepth == maxSearchDepth)
            return;

        List<String> childLinks = retrieveLinks(url);
        for(String link : childLinks)
            crawl(link, linkDepth + 1);
    }

    private List<String> retrieveLinks(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> linkList = new ArrayList<String>();

        Elements questions = doc.select("a[href]");
        for(Element linkElement: questions){
            String link = linkElement.attr("abs:href");
            if (!crawledLinks.contains(link)) {
                linkList.add(link);
                crawledLinks.add(link);
            }
        }

        return linkList;
    }

    private void processLink(String url, int linkDepth) {
        //just print
        for (int i=0; i < linkDepth; i++) {
            System.out.print("     ");
        }
        System.out.println(url);
    }
}
