package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.model.Node;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.util.NodeCrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by xing on 2/28/15.
 */
public class NodeThread implements Runnable {

    private final int MAX_PRODUCT_THREAD_           = 4;
    private final int MAX_REVIEW_THREAD_            = 8;
    private final int MAX_AWAIT_HOURS_4_PRODUCT_    = 5;
    private final int MAX_AWAIT_HOURS_4_REVIEW_  = 16;


    private final String brand_;

    public NodeThread(String brand) {
        this.brand_ = brand;
    }

    private boolean isNewEnough() {
        return false;
    }


    @Override
    public void run() {
        // obtain last update date from database

        // check if it is new enough to skip new crawling
        if (!isNewEnough()) {
            NodeCrawler nc = new NodeCrawler();
            Map<String, Node> nodes = nc.crawl();

            System.out.println("Crawled " + nodes.size() + " nodes");

            // TODO: update database

            // send out product crawler for each node id
            ExecutorService prodcutExecutor
                    = Executors.newFixedThreadPool(MAX_PRODUCT_THREAD_);
            List<Future<Map<String, Product>>> futureProducts = new ArrayList<>();
            for (String nodeId : nodes.keySet())
                futureProducts.add(prodcutExecutor.submit(new ProductThread(brand_, nodeId)));

            // shutdown executor once all the tasks are done
            prodcutExecutor.shutdown();
            try {
                prodcutExecutor.awaitTermination(MAX_AWAIT_HOURS_4_PRODUCT_, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // merge all the results together
            Map<String, Product> allProducts = new HashMap<>();
            for (Future<Map<String, Product>> futureProduct : futureProducts) {
                try {
                    allProducts.putAll(futureProduct.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }


            System.out.println("Crawled " + allProducts.size() + " products");

            // TODO: update database

            // send out review crawler for each product
            ExecutorService reviewExecutor
                    = Executors.newFixedThreadPool(MAX_REVIEW_THREAD_);
            Map<String, Future<Map<String, Review>>> futureReviews = new HashMap<>();
            for (String productId : allProducts.keySet())
                futureReviews.put(productId, reviewExecutor.submit(new ReviewThread(productId)));

            // shutdown executor once all the tasks are done
            reviewExecutor.shutdown();
            try {
                reviewExecutor.awaitTermination(MAX_AWAIT_HOURS_4_REVIEW_, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // merge all the results together
            Map<String, Map<String, Review>> allReviews = new HashMap<>();
            for (String productId : futureReviews.keySet()) {
                try {
                    allReviews.put(productId, futureReviews.get(productId).get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                System.out.println("Crawled " + allReviews.get(productId).size()
                        + " reivews for product " + productId);
            }

            // TODO: update database
        }
    }
}
