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

class Link {
    String url;
    int linkDepth;
    String parentUrl;

    public Link(String url, int linkDepth, String parentUrl) {
        this.url = url;
        this.linkDepth = linkDepth;
        this.parentUrl = parentUrl;
    }
}

class PageProcessTask implements Runnable {

    Link link;
    CountDownLatch parentCdl;

    public PageProcessTask(Link link, CountDownLatch parentCdl) {
        this.link = link;
        this.parentCdl = parentCdl;
    }

    public void run() {
        CrawlerConcurrent.processLink(link);

        if (link.linkDepth == CrawlerConcurrent.maxSearchDepth) {
            parentCdl.countDown();
            return;
        }

        List<Link> childLinks = retrieveLinks(link.url);
        CountDownLatch cdl = new CountDownLatch(childLinks.size());

        for(Link childLink : childLinks) {
            PageProcessTask task = new PageProcessTask(childLink, cdl);
            new Thread(task).start();
        }

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parentCdl.countDown();
    }

    private List<Link> retrieveLinks(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Link> linkList = new ArrayList<Link>();

        Elements questions = doc.select("a[href]");
        for(Element linkElement: questions){
            String linkUrl = linkElement.attr("abs:href");
            if (!CrawlerConcurrent.crawledLinks.contains(linkUrl)) {
                linkList.add(new Link(linkUrl, this.link.linkDepth + 1, url));
                CrawlerConcurrent.crawledLinks.add(linkUrl);
            }
        }

        return linkList;
    }
}

public class CrawlerConcurrent {

    static int maxSearchDepth;
    static HashSet<String> crawledLinks = new HashSet<String>();
    static private LinkTree linkTree;

    public CrawlerConcurrent(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
    }

    public int getcrawledLinksSize() {
        return crawledLinks.size();
    }

    public void crawl(String startUrl) {
        long start = System.currentTimeMillis();

        crawledLinks.clear();
        crawledLinks.add(startUrl);

        CountDownLatch cdl = new CountDownLatch(1);

        Link startLink = new Link(startUrl, 0, "");
        linkTree = new LinkTree(new Node(startLink));

        PageProcessTask task = new PageProcessTask(startLink, cdl);
        new Thread(task).start();

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        linkTree.print();

        long finish = System.currentTimeMillis();
        System.out.println("--------------------------------------------------");
        System.out.println(finish - start + " ms");
        System.out.println(getcrawledLinksSize() + " links");
    }

    public static synchronized void processLink(Link link) {
        //add to link tree
        linkTree.add(link.parentUrl, link.url, link.linkDepth);
    }
}
