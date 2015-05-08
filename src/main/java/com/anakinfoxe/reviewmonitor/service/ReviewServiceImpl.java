package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.repository.BrandRepository;
import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
