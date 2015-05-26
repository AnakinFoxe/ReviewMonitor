package com.anakinfoxe.reviewmonitor.service;

/**
 * Created by xing on 3/3/15.
 */
public interface CrawlerService {

    int crawlBrand(String brand) throws Exception;

    void crawlProduct(String productId);
}
