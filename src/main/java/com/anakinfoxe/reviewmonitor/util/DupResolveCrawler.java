package com.anakinfoxe.reviewmonitor.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * This crawler targets to crawl the review page through review's permalink.
 * It is intend to determine the product id relates to this review.
 *
 * Created by xing on 5/19/15.
 */
public class DupResolveCrawler {

    // CSS Query string
    private final String Q_PRODUCT_        = "span.asinReviewsSummary";


    private int maxRetries_ = 10;


    public String crawl(String permalink) {
        if (permalink == null)
            return null;

        for (int retry = 1; retry <= maxRetries_; ++retry) {


            try {
                // get the web page source
                Document page = Jsoup.connect(permalink).get();

                Element product = page.select(Q_PRODUCT_).first();
                if (product != null)
                    return product.attr("name");

            } catch (IOException e) {
                System.out.println("IOException (DupResolveCrawler). Retrying "
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
}
