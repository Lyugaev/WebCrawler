package net.lyugaev.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmitry on 15.09.16.
 */
public class Crawler {

    private final static String START_URL = "https://ru.wikipedia.org/wiki/SOAP";
    private final String LINK_PATTERN = "<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]";
    private final int MAX_DEPTH = 2;

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.search(START_URL, 1);
    }

    void search(String urlStr, int depth) {
        if (depth > MAX_DEPTH)
            return;

        URL url = validateUrl(urlStr);
        if (url != null) {
            String pageContent = downloadPage(url);
            ArrayList<String> childLinks = findLinks(pageContent);
        }
    }

    URL validateUrl(String url) {
        if (!url.toLowerCase().startsWith("http://")
                && !url.toLowerCase().startsWith("https://")
            )
            return null;

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    String downloadPage(URL pageUrl) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));

            String line;
            StringBuffer pageBuffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                pageBuffer.append(line);
            }

            return pageBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    ArrayList findLinks(String pageContent) {
        Pattern pattern = Pattern.compile(LINK_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pageContent);

        ArrayList<String> linkList = new ArrayList<String>();
        while (matcher.find()) {
            String link = matcher.group(1).trim();

            System.out.println(link);
        }

        return linkList;
    }
}
