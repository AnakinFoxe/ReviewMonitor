package com.anakinfoxe.reviewmonitor.util;

import com.anakinfoxe.reviewmonitor.model.Review;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This crawler will crawl the specified review page and identify whether the last replied user
 * is Customer Service or not
 *
 * Created by xing on 6/2/15.
 */
public class StatusCrawler {

    // CSS Query string
    private final String Q_PAGE_            = "div.cdPageSelectorPagination";
    private final String Q_USER_            = "div.postFrom";

    // Regular Expression Patterns
    private final Pattern PTN_USER_         = Pattern.compile("'(.+?)'\\)");


    private int maxRetries_ = 10;


    public Review.Status crawl(String permalink, Set<String> monitoredUsers) {
        if (permalink == null)
            return null;

        for (int retry = 1; retry <= maxRetries_; ++retry) {


            try {
                // get the web page source
                Document page = Jsoup.connect(permalink).get();

                // get the pagination info
                Element pagination = page.select(Q_PAGE_).first();
                if (pagination != null) {
                    Elements commentPages = pagination.select("a");

                    String username;
                    if (commentPages.size() == 0) {
                        username = scrapeUsername(page);
                    } else {
                        // 2 or more comment pages
                        Element lastPage = commentPages.get(commentPages.size() - 2);   // the last one will be "next page"

                        String lastPageUrl = lastPage.attr("href");

                        // scrape the last page
                        username = scrapeLastPage(lastPageUrl);
                    }

                    if (username != null && monitoredUsers.contains(username))
                        return Review.Status.REPLIED;
                }

                return Review.Status.NEEDS_REPLY;

            } catch (IOException e) {
                System.out.println("IOException (StatusCrawler). Retrying "
                        + retry + "/" + maxRetries_
                        + ": " + permalink);

                // incremental waiting
                try {
                    Thread.sleep(3000 + 5000 * retry);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // TODO: when reaches maxRetries, do something?
            }
        }

        return Review.Status.NEEDS_REPLY;
    }

    private String scrapeLastPage(String url) {
        if (url == null)
            return null;

        for (int retry = 1; retry <= maxRetries_; ++retry) {
            try {
                // get the web page source
                Document page = Jsoup.connect(url).get();

                return scrapeUsername(page);
            } catch (IOException e) {
                System.out.println("IOException (StatusCrawler.LastPage). Retrying "
                        + retry + "/" + maxRetries_
                        + ". " + e.getMessage());

                // incremental waiting
                try {
                    Thread.sleep(3000 + 5000 * retry);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // TODO: when reaches maxRetries, do something?
            }
        }

        return null;
    }

    private String scrapeUsername(Document page) {
        try {
            Element user = page.select(Q_USER_).last();
            Element test = user.select("a").first();

            return test.ownText().trim();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
