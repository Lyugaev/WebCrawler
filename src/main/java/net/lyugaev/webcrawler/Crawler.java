package net.lyugaev.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by dmitry on 15.09.16.
 */
public class Crawler {

    private int maxSearchDepth;

    public Crawler(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
    }

    void search(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements questions = doc.select("a[href]");
        for(Element link: questions){
            System.out.println(link.attr("abs:href"));
        }
    }
}
