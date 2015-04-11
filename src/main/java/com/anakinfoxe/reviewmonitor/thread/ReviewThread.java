package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.util.ReviewCrawler;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by xing on 2/28/15.
 */
public class ReviewThread implements Callable<Map<String, Review>> {

    private final String productId_;

    public ReviewThread(String productId) {
        this.productId_ = productId;
    }


    @Override
    public Map<String, Review> call() throws Exception {

        ReviewCrawler rc = new ReviewCrawler();
        return rc.crawl(productId_);

    }
}
