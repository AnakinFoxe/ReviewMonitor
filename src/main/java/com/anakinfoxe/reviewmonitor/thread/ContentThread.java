package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.util.ContentCrawler;

import java.util.concurrent.Callable;

/**
 * Created by xing on 5/20/15.
 */
public class ContentThread implements Callable<String> {

    private final String permalink_;

    public ContentThread(String permalink) {
        this.permalink_ = permalink;
    }


    @Override
    public String call() throws Exception {
        ContentCrawler cc = new ContentCrawler();
        return cc.crawl(permalink_);
    }
}
