package net.lyugaev.webcrawler;

/**
 * Created by dmitry on 17.09.16.
 */
public class CrawlerMain {

    static final String START_URL = "https://spring.io/guides";
    static final int MAX_DEPTH = 2;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Crawler crawler = new Crawler(MAX_DEPTH);
        crawler.search(START_URL);

        long finish = System.currentTimeMillis();
        System.out.println(finish - start + " ms");
    }
}
