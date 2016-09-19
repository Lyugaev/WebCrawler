package net.lyugaev.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dmitry on 15.09.16.
 */

class PageProcessTask implements Runnable {

    String link;
    int linkDepth;
    CountDownLatch parentCdl;

    public PageProcessTask(String link, int linkDepth, CountDownLatch parentCdl) {
        this.link = link;
        this.linkDepth = linkDepth;
        this.parentCdl = parentCdl;
    }

    public void run() {
        processLink(this.link, linkDepth);

        if (linkDepth == CrawlerConcurrent.maxSearchDepth) {
            parentCdl.countDown();
            return;
        }

        List<String> childLinks = retrieveLinks(this.link);
        CountDownLatch cdl = new CountDownLatch(childLinks.size());

        for(String link : childLinks) {
            PageProcessTask task = new PageProcessTask(link, linkDepth + 1, cdl);
            new Thread(task).start();
        }

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parentCdl.countDown();
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
            if (!CrawlerConcurrent.crawledLinks.contains(link)) {
                linkList.add(link);
                CrawlerConcurrent.crawledLinks.add(link);
            }
        }

        return linkList;
    }

    private void processLink(String link, int linkDepth) {
        //just print
        for (int i=0; i < linkDepth; i++) {
            System.out.print("     ");
        }
        System.out.println(link);
    }
}

public class CrawlerConcurrent {

    static int maxSearchDepth;
    static HashSet<String> crawledLinks = new HashSet<String>();

    public CrawlerConcurrent(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
    }

    public int getcrawledLinksSize() {
        return crawledLinks.size();
    }

    public void crawl(String startLink) {
        long start = System.currentTimeMillis();

        crawledLinks.clear();
        crawledLinks.add(startLink);

        CountDownLatch cdl = new CountDownLatch(1);
        PageProcessTask task = new PageProcessTask(startLink, 0, cdl);
        new Thread(task).start();

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long finish = System.currentTimeMillis();
        System.out.println("--------------------------------------------------");
        System.out.println(finish - start + " ms");
        System.out.println(getcrawledLinksSize() + " links");
    }
}
