package com.anakinfoxe.reviewmonitor.resource;

import com.anakinfoxe.reviewmonitor.model.Crawler;
import com.anakinfoxe.reviewmonitor.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Created by xing on 3/3/15.
 */
@Path("/crawl")
public class CrawlerResource {

    @Autowired
    CrawlerService crawlerService;

    private static Map<String, Crawler> crawlerPool_ = new HashMap<>();

    private final int MAX_ALLOWED_CRAWLER_ = 5;

    // TODO: might remove this from code
    private String key_ = "WhatAGoodDay!!";


    @Path("{brand}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startCrawler(@PathParam("brand") String brand,
                               @DefaultValue("") @QueryParam("key") String key) {

        if (!key.equals(key_))
            return "Key is either not present or not correct.";

        // TODO: to be honest, this is a risky implementation
        final String b_ = brand;
        new Thread(new Runnable() {

            @Override
            public void run() {
                crawlBrand(b_);
            }

            // Crawler routine in the thread
            private void crawlBrand(String brand) {
                // Make sure there's empty spot
                if (poolIsFull()) {
                    System.out.println("No more spot for crawling " + brand);
                    return;
                }
                // Make sure this is a new brand to crawl
                if (alreadyExists(brand)) {
                    System.out.println(brand + " crawler already exists");
                    return;
                }

                // add the brand to crawler pool
                prepare(brand);

                // record current time
                Date startDate = new Date();

                try {
                    // start the crawler
                    startRunning(brand);
                    startCrawling(brand);

                    int cnt = 0;
                    while (true) {
                        System.out.println(brand + " crawler lives " + cnt + " rounds...");

                        // start crawling if needed
                        if (needCrawling(brand)) {
                            System.out.println(brand + " crawler is crawling!!!");
                            crawlerService.crawlBrand(brand);
                        }

                        ++cnt;

                        // crawling setup for next time
                        if (isOneDayLater(startDate)) {
                            startDate = new Date(); // update

                            startCrawling(brand);
                        } else
                            stopCrawling(brand);

                        // stop the crawler if there's no need to run
                        if (!needRunning(brand)) {
                            System.out.println(brand + " crawler stopped");
                            break;
                        } else {
                            // sleep 10 min
                            System.out.println(brand + " crawler is sleeping...");
                            Thread.sleep(1000 * 60 * 10);
                        }

                    }

                    // retrieve pool resource
                    cleanup(brand);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return brand + " crawling started.";
    }

    @Path("/stop/{brand}")
    @GET
    public void stopCrawler(@PathParam("brand") String brand,
                               @DefaultValue("") @QueryParam("key") String key) {
        if (key.equals(key_)) {
            stopRunning(brand);
        }

    }


    @Path("/status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Crawler> getCrawlerStatus() {
        return getAll();
    }


    /**
     *  Private Methods for Multi-threading
     */

    private synchronized boolean poolIsFull() {
        return crawlerPool_.size() >= MAX_ALLOWED_CRAWLER_;
    }

    private synchronized boolean alreadyExists(String brand) {
        return crawlerPool_.containsKey(brand);
    }

    private synchronized void prepare(String brand) {
        Crawler crawler = new Crawler(brand);
        crawlerPool_.put(brand, crawler);
    }

    private synchronized void cleanup(String brand) {
        crawlerPool_.remove(brand);
    }

    private synchronized void startCrawling(String brand) {
        Crawler crawler = crawlerPool_.get(brand);
        crawler.setIsCrawling(true);
    }

    private synchronized void startRunning(String brand) {
        Crawler crawler = crawlerPool_.get(brand);
        crawler.setIsRunning(true);
    }

    private synchronized void stopCrawling(String brand) {
        Crawler crawler = crawlerPool_.get(brand);
        crawler.setIsCrawling(false);
    }

    private synchronized void stopRunning(String brand) {
        Crawler crawler = crawlerPool_.get(brand);
        crawler.setIsRunning(false);
    }

    private synchronized boolean needCrawling(String brand) {
        Crawler crawler = crawlerPool_.get(brand);
        return crawler.isCrawling();
    }

    private synchronized boolean needRunning(String brand) {
        Crawler crawler = crawlerPool_.get(brand);
        return crawler.isRunning();
    }

    private synchronized List<Crawler> getAll() {
        List<Crawler> crawlers = new ArrayList<>();
        for (String brand : crawlerPool_.keySet())
            crawlers.add(crawlerPool_.get(brand));

        return crawlers;
    }


    private boolean isOneDayLater(Date startDate) {
        Date currentDate = new Date();
        long elapsed = currentDate.getTime() - startDate.getTime();
        long oneDay = 1000 * 3600 * 24;

        return elapsed > oneDay;
    }




}
