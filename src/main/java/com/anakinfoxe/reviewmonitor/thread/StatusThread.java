package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.util.StatusCrawler;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by xing on 6/3/15.
 */
public class StatusThread implements Callable<Review.Status> {

    private final String permalink_;
    private final Set<String> monitoredUsers_;

    public StatusThread(String permalink_, Set<String> monitoredUsers_) {
        this.permalink_ = permalink_;
        this.monitoredUsers_ = monitoredUsers_;
    }


    @Override
    public Review.Status call() throws Exception {
        StatusCrawler sc = new StatusCrawler();
        return sc.crawl(permalink_, monitoredUsers_);
    }
}
