package net.lyugaev.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    int maxSearchDepth;
    Set<String> crawledLinks;
    CountDownLatch parentCdl;
    ExecutorService threadPool;

    public PageProcessTask(Link link, int maxSearchDepth, Set<String> crawledLinks, CountDownLatch parentCdl, ExecutorService threadPool) {
        this.link = link;
        this.maxSearchDepth = maxSearchDepth;
        this.crawledLinks = crawledLinks;
        this.parentCdl = parentCdl;
        this.threadPool = threadPool;
    }

    public void run() {
        //processLink(link);

        if (link.linkDepth < maxSearchDepth) {

            List<Link> childLinks = retrieveLinks(link.url);
            if (!childLinks.isEmpty()) {

                CountDownLatch cdl = new CountDownLatch(childLinks.size());

                for (Link childLink : childLinks) {
                    PageProcessTask task = new PageProcessTask(childLink, maxSearchDepth, crawledLinks, cdl, threadPool);
                    threadPool.submit(task);
                }

                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        parentCdl.countDown();
    }

    private List<Link> retrieveLinks(String urlStr) {
        List<Link> linkList = new ArrayList<Link>();

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            return linkList;
        }
        Document doc = null;
        try {
            doc = Jsoup.parse(url, 0);
        } catch (IOException e) {
//            e.printStackTrace();
            return linkList;
        }

        Elements questions = doc.select("a[href]");
        for(Element linkElement: questions){
            String linkUrl = linkElement.attr("abs:href");
            if (!crawledLinks.contains(linkUrl)) {
                linkList.add(new Link(linkUrl, this.link.linkDepth + 1, urlStr));
                crawledLinks.add(linkUrl);
            }
        }

        return linkList;
    }

    private void processLink(Link link) {
        //System.out.println(link.url);
        //add to link tree
        //linkTree.add(link.parentUrl, link.url, link.linkDepth);
    }
}

public class CrawlerConcurrent {

    private Set<String> crawledLinks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private ExecutorService threadPool = Executors.newCachedThreadPool();
//    private LinkTree linkTree;

    public int getcrawledLinksSize() {
        return crawledLinks.size();
    }

    public void crawl(String startUrl, int maxSearchDepth) {
        long start = System.currentTimeMillis();

        crawledLinks.clear();
        crawledLinks.add(startUrl);

        CountDownLatch cdl = new CountDownLatch(1);

        Link startLink = new Link(startUrl, 0, "");
//        linkTree = new LinkTree(new Node(startLink));

        PageProcessTask task = new PageProcessTask(startLink, maxSearchDepth, crawledLinks, cdl, threadPool);
        threadPool.submit(task);

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();

//        linkTree.print();

        long finish = System.currentTimeMillis();
        System.out.println("--------------------------------------------------");
        System.out.println(finish - start + " ms");
        System.out.println(getcrawledLinksSize() + " links");
    }
}
