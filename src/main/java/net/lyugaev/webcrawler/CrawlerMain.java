package net.lyugaev.webcrawler;

/**
 * Created by dmitry on 17.09.16.
 */
public class CrawlerMain {

    static final String START_URL = "https://spring.io/guides";
    static final int MAX_LINK_DEPTH = 2;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Crawler crawler = new Crawler(MAX_LINK_DEPTH);
        crawler.crawl(START_URL, 0);

        long finish = System.currentTimeMillis();
        System.out.println("--------------------------------------------------");
        System.out.println(finish - start + " ms");
        System.out.println(crawler.getTotalLinkCount() + " links");
    }
}
