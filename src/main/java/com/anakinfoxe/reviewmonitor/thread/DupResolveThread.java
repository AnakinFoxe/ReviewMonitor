package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.util.DupResolveCrawler;

import java.util.concurrent.Callable;

/**
 * Created by xing on 5/20/15.
 */
public class DupResolveThread implements Callable<String> {

    private final String permalink_;

    public DupResolveThread(String permalink) {
        this.permalink_ = permalink;
    }


    @Override
    public String call() throws Exception {
        DupResolveCrawler cc = new DupResolveCrawler();
        return cc.crawl(permalink_);
    }
}
