package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Node;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.repository.BrandRepository;
import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;
import com.anakinfoxe.reviewmonitor.thread.ProductThread;
import com.anakinfoxe.reviewmonitor.thread.ReviewThread;
import com.anakinfoxe.reviewmonitor.util.NodeCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by xing on 3/3/15.
 */
@Service("crawlerService")
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    private final int MAX_PRODUCT_THREAD_           = 4;
    private final int MAX_REVIEW_THREAD_            = 8;
    private final int MAX_AWAIT_HOURS_4_PRODUCT_    = 5;
    private final int MAX_AWAIT_HOURS_4_REVIEW_     = 16;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int crawlBrand(String brand) {
        // a little pre-processing
        brand = brand.toLowerCase().trim();

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

        Brand brandObj = null;
        // if there's no product crawled, it's possible the brand name is incorrect
        if (allProducts.size() == 0)
            return 0;
        else {
            brandObj = brandRepository.loadByName(brand);

            if (brandObj == null) {
                brandObj = brandRepository.save(new Brand(brand));
            }
        }

        // send out review crawler for each product
        ExecutorService reviewExecutor
                = Executors.newFixedThreadPool(MAX_REVIEW_THREAD_);
        Map<String, Future<Map<String, Review>>> futureReviews = new HashMap<>();
        for (Product product : allProducts.values()) {
            // update brand mapping
            product.setBrand(brandObj);

            // check database info
            Product savedProduct = productRepository.loadByProductId(product.getProductId());

            if (savedProduct != null) {
                // if review number does not change, do not crawl
                if (savedProduct.getNumOfReviewsOnPage().equals(product.getNumOfReviewsOnPage()))
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

        // load all the saved reviews of this brand and convert into set
        List<Review> savedReviews = reviewRepository.loadAllByBrand(brandObj);
        Set<String> savedReviewsSet = new HashSet<>();
        for (Review review : savedReviews)
            savedReviewsSet.add(review.getName());

        // update database
        for (String productId : allReviews.keySet()) {
            // get saved reviews from database
            Product savedProduct = productRepository.loadByProductId(productId);

            // get lately obtained reviews from results
            Map<String, Review> productReviews = allReviews.get(productId);

            // Remove already saved reviews (optimized a little bit)
            for (String reviewName : productReviews.keySet()) {
                if (savedReviewsSet.contains(reviewName))
                    productReviews.remove(reviewName);
            }

            // insert rest (new) reviews
            for (Review review : productReviews.values()) {
                // update product mapping
                review.setProduct(savedProduct);
                // update brand mapping
                review.setBrand(brandObj);
                // update modelNum
                if (savedProduct.getModelNum() == null)
                    review.setModelNum("Model # empty");
                else
                    review.setModelNum(savedProduct.getModelNum());

                reviewRepository.save(review);
            }
        }

        return allReviews.size();   // number of products with reviews crawled
    }

    @Override
    public void crawlProduct(String productId) {

    }
}
