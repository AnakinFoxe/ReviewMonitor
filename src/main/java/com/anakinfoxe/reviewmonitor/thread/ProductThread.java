package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.util.ProductCrawler;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by xing on 2/28/15.
 */
public class ProductThread implements Callable<Map<String, Product>> {

    private final String brand_;
    private final String nodeId_;

    public ProductThread(String brand, String nodeId) {
        this.brand_ = brand;
        this.nodeId_ = nodeId;
    }


    @Override
    public Map<String, Product> call() throws Exception {
        ProductCrawler pc = new ProductCrawler();
        return pc.crawl(brand_, nodeId_);
    }
}
