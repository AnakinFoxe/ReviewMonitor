package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Node;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.repository.BrandRepository;
import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;
import com.anakinfoxe.reviewmonitor.thread.DupResolveThread;
import com.anakinfoxe.reviewmonitor.thread.ProductThread;
import com.anakinfoxe.reviewmonitor.thread.ReviewThread;
import com.anakinfoxe.reviewmonitor.thread.StatusThread;
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
    private final int MAX_RESOLVER_THREAD_          = 8;
    private final int MAX_STATUS_THREAD_            = 8;

    private final int MAX_AWAIT_HOURS_4_PRODUCT_    = 2;
    private final int MAX_AWAIT_HOURS_4_REVIEW_     = 4;
    private final int MAX_AWAIT_HOURS_4_RESOLVER_   = 4;
    private final int MAX_AWAIT_HOURS_4_STATUS_     = 4;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int crawlBrand(String brand) throws Exception {
        // a little pre-processing
        brand = brand.toLowerCase().trim();

        /**
         * Update Node id information
         */

        // get latest node info every time
        NodeCrawler nc = new NodeCrawler();
        Map<String, Node> nodes = nc.crawl();

        System.out.println("Crawled " + nodes.size() + " nodes");

        /**
         * Obtain all the products of this brand
         */

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

        /**
         * Crawl reviews of the products whose number of reviews were changed
         */

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

        /**
         * Remove reviews which already in the database from the crawling result list.
         * Also keep track of duplicated reviews in the crawling result list.
         */

        // load all the saved reviews of this brand and convert into set
        List<Review> savedReviews = reviewRepository.loadAllByBrand(brandObj);
        Set<String> savedReviewsSet = new HashSet<>();
        // to be monitored reviews (status needs to be update)
        Map<String, Review> monitoredReviews = new HashMap<>();
        for (Review review : savedReviews) {
            // construct set for reviews in database
            savedReviewsSet.add(review.getName());

            // record reviews to be monitored
            if (review.getStatus().equals(Review.Status.REPLIED))
                monitoredReviews.put(review.getName(), review);
        }

        // to be saved reviews
        Map<String, Review> reviewsToBeSaved = new HashMap<>();
        // need to resolve (duplicate)
        Map<String, Review> reviewsNeedToResolve = new HashMap<>();

        // update review information and identify duplicates
        for (String productId : allReviews.keySet()) {
            // get saved reviews from database
            Product savedProduct = productRepository.loadByProductId(productId);

            // get lately obtained reviews from results
            Map<String, Review> productReviews = allReviews.get(productId);

            // Remove already saved reviews (optimized a little bit)
            Set<String> duplicates = new HashSet<>();
            for (String reviewName : productReviews.keySet()) {
                if (savedReviewsSet.contains(reviewName))
                    duplicates.add(reviewName);
            }
            for (String reviewName : duplicates)
                productReviews.remove(reviewName);

            // insert rest (new) reviews
            for (Review review : productReviews.values()) {
                // set product mapping
                review.setProduct(savedProduct);
                // set brand mapping
                review.setBrand(brandObj);
                // set modelNum
                if (savedProduct.getModelNum() == null)
                    review.setModelNum("Model # empty");
                else
                    review.setModelNum(savedProduct.getModelNum());
                // set crawled times
                review.setCrawledTimes(0);
                // set status
                review.setStatus(Review.Status.NEEDS_REPLY);

                if (!reviewsToBeSaved.containsKey(review.getName()))
                    reviewsToBeSaved.put(review.getName(), review);
                else
                    reviewsNeedToResolve.put(review.getName(), review);
            }
        }

        /**
         * Resolve duplicated reviews in the crawling result list
         */

        // 1. crawl review content page
        ExecutorService resolverExecutor
                = Executors.newFixedThreadPool(MAX_RESOLVER_THREAD_);
        Map<String, Future<String>> futureProductId = new HashMap<>();
        for (String reviewName : reviewsNeedToResolve.keySet()) {
            futureProductId.put(reviewName, resolverExecutor.submit(
                    new DupResolveThread(reviewsNeedToResolve.get(reviewName).getPermalink())));
        }

        // 2. shutdown executor once all the tasks are done
        resolverExecutor.shutdown();
        try {
            resolverExecutor.awaitTermination(MAX_AWAIT_HOURS_4_RESOLVER_, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. update correct product info for the review
        for (String reviewName : futureProductId.keySet()) {
            try {
                String productId = futureProductId.get(reviewName).get();

                // get saved reviews from database
                Product savedProduct = productRepository.loadByProductId(productId);

                Review review = reviewsToBeSaved.get(reviewName);
                // update correct product
                review.setProduct(savedProduct);
                // update correct modelNum
                if (savedProduct == null)
                    review.setModelNum("Outdated Model");
                else if (savedProduct.getModelNum() == null)
                    review.setModelNum("Model # empty");
                else
                    review.setModelNum(savedProduct.getModelNum());


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // save new reviews to database
        for (Review review : reviewsToBeSaved.values())
            reviewRepository.save(review);

        /**
         * Track review status
         */
        // include all new reviews and reviews in "REPLIED" status
        monitoredReviews.putAll(reviewsToBeSaved);

        // monitored user names (Customer Service names)
        Set<String> monitoredUsers = new HashSet<>();
        monitoredUsers.add("Support Volcus");
        monitoredUsers.add("Support Customer Service");

        ExecutorService statusExecutor
                = Executors.newFixedThreadPool(MAX_STATUS_THREAD_);
        Map<String, Future<Review.Status>> futureStatus = new HashMap<>();
        for (Review review : monitoredReviews.values()) {
            futureStatus.put(review.getName(),
                    statusExecutor.submit(new StatusThread(review.getPermalink(), monitoredUsers)));
        }

        // shutdown executor
        statusExecutor.shutdown();
        try {
            statusExecutor.awaitTermination(MAX_AWAIT_HOURS_4_STATUS_, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // update status
        for (String reviewName : futureStatus.keySet()) {
            try {
                Review.Status status = futureStatus.get(reviewName).get();

                Review review = monitoredReviews.get(reviewName);
                // update status to the review
                review.setStatus(status);
                // update crawled number
                if (status.equals(Review.Status.REPLIED)) {
                    review.setCrawledTimes(review.getCrawledTimes() + 1);
                } else {
                    review.setCrawledTimes(0);  // no longer needs to be crawled
                }

                // has been crawled too many times
                if (review.getCrawledTimes() >= 180)
                    review.setStatus(Review.Status.OUTDATED);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // update to database
        for (Review review : monitoredReviews.values())
            reviewRepository.saveOrUpdate(review);

        return reviewsToBeSaved.size();   // number of products with reviews crawled
    }

    @Override
    public void crawlProduct(String productId) {

    }
}
