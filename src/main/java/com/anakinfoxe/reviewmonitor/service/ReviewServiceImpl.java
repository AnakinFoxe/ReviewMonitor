package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.repository.BrandRepository;
import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xing on 5/2/15.
 */
@Service("reviewService")
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public List<Review> loadAllByBrandId(Long brandId) {
        Brand brand = brandRepository.loadById(brandId);

        if (brand != null) {
            return reviewRepository.loadAllByBrand(brand);
        }

        return null;
    }

    @Override
    public List<Review> loadLatestByBrandId(Long brandId, int pageNum, int pageSize) {
        Brand brand = brandRepository.loadById(brandId);

        if (brand != null) {
            return reviewRepository.loadAllByBrand(brand);
        }

        return null;
    }

    @Override
    public int deleteAllByBrandId(Long brandId) {
        Brand brand = brandRepository.loadById(brandId);

        if (brand != null)
            return reviewRepository.deleteByBrand(brand);

        return 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void changeReviewToReplied(Long reviewId) {
        Review review = reviewRepository.loadById(reviewId);

        if (review != null) {
            review.setStatus(Review.Status.REPLIED);
            reviewRepository.saveOrUpdate(review);
        }
    }


}
