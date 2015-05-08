package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;

import java.util.List;

/**
 * Created by xing on 5/2/15.
 */
public interface ReviewService {

    List<Review> loadAllByBrandId(Long brandId);

    List<Review> loadLatestByBrandId(Long brandId, int pageNum, int pageSize);
}
