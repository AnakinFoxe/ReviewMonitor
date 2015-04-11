package com.anakinfoxe.reviewmonitor.thread;

import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xing on 2/28/15.
 */
public class MonitorThread implements Runnable {

    // TODO: not used atm

    private final ProductRepository productRepository_;

    private final ReviewRepository reviewRepository_;

    private Long checkPoint = 0L;

    private final Long CHECK_INTERVAL_ = 10 * 60 * 1000L;
    private final Long NODE_INTERVAL_ = 3600 * 24 * 1000L;

    private String brand_;

    public MonitorThread(String brand,
                         ProductRepository productRepository,
                         ReviewRepository reviewRepository) {
        this.brand_ = brand;
        this.productRepository_ = productRepository;
        this.reviewRepository_ = reviewRepository;
    }

    private boolean isLongEnough() {
        return System.currentTimeMillis() - checkPoint > NODE_INTERVAL_;
    }

    @Override
    public void run() {

        // repetitively send out node crawler
        while (true) {
            try {
                // waited long enough, send out node crawler
                if (isLongEnough()) {
                    checkPoint = System.currentTimeMillis();

                    Thread crawler = new Thread(new NodeThread());
                    crawler.start();
                }

                // wait awhile
                Thread.sleep(CHECK_INTERVAL_);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(1);
//
//        executorService.submit(new MonitorThread("soundbot"));
//
//        executorService.shutdown();
    }
}
