package net.lyugaev.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitry on 15.09.16.
 */
public class Crawler {

    private int maxSearchDepth;
    private int totalLinkCount = 0;

    public Crawler(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
    }

    public int getTotalLinkCount() {
        return totalLinkCount;
    }

    public void crawl(String url, int linkDepth) {
        printLink(url, linkDepth);
        totalLinkCount++;

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
        for(Element link: questions){
            linkList.add(link.attr("abs:href"));
        }

        return linkList;
    }

    private void printLink(String url, int linkDepth) {
        for (int i=0; i < linkDepth; i++) {
            System.out.print("     ");
        }
        System.out.println(url);
    }
}
