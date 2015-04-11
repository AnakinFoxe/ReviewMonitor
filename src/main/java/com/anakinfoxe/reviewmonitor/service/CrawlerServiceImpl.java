package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Node;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.repository.NodeRepository;
import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;
import com.anakinfoxe.reviewmonitor.thread.MonitorThread;
import com.anakinfoxe.reviewmonitor.thread.ProductThread;
import com.anakinfoxe.reviewmonitor.thread.ReviewThread;
import com.anakinfoxe.reviewmonitor.util.NodeCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by xing on 3/3/15.
 */
@Service("crawlerService")
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    private final int MAX_PRODUCT_THREAD_           = 4;
    private final int MAX_REVIEW_THREAD_            = 8;
    private final int MAX_AWAIT_HOURS_4_PRODUCT_    = 5;
    private final int MAX_AWAIT_HOURS_4_REVIEW_     = 16;


    @Transactional
    public void crawlBrand(String brand) {

        // get latest node info every time
        NodeCrawler nc = new NodeCrawler();
        Map<String, Node> nodes = nc.crawl();

        System.out.println("Crawled " + nodes.size() + " nodes");

        // send out product crawler for each node id
        ExecutorService prodcutExecutor
                = Executors.newFixedThreadPool(MAX_PRODUCT_THREAD_);
        List<Future<Map<String, Product>>> futureProducts = new ArrayList<>();
        for (String nodeId : nodes.keySet())
            futureProducts.add(prodcutExecutor.submit(new ProductThread(brand, nodeId)));

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

        // send out review crawler for each product
        ExecutorService reviewExecutor
                = Executors.newFixedThreadPool(MAX_REVIEW_THREAD_);
        Map<String, Future<Map<String, Review>>> futureReviews = new HashMap<>();
        for (Product product : allProducts.values()) {
            Product savedProduct = productRepository.loadByProductId(product.getProductId());

            if (savedProduct != null) {
                // if review number does not change, do not crawl
                if (savedProduct.getNumOfReviewsOnPage() == product.getNumOfReviewsOnPage())
                    continue;

                // update database since product info changed
                product.setId(savedProduct.getId());
                productRepository.saveOrUpdate(product);
            } else {
                // insert into database since no previous record
                productRepository.save(product);
            }

            // only crawl those products whose review number changed
            // or the product is new
            String productId = product.getProductId();
            futureReviews.put(productId, reviewExecutor.submit(new ReviewThread(productId)));
        }

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

        // update database
        for (String productId : allReviews.keySet()) {
            // get saved reviews from database
            Product savedProduct = productRepository.loadByProductId(productId);
            List<Review> savedReviews = reviewRepository.loadAllByProduct(savedProduct);

            // get lately obtained reviews from results
            Map<String, Review> productReviews = allReviews.get(productId);

            // Remove already saved reviews
            for (Review review : savedReviews) {
                String reviewName = review.getName();
                if (productReviews.containsKey(reviewName))
                    productReviews.remove(reviewName);
            }

            // insert rest (new) reviews
            for (Review review : productReviews.values())
                reviewRepository.save(review);
        }

    }

    @Override
    public void crawlProduct(String productId) {

    }
}
