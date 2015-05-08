package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;

import java.util.List;

/**
 * Created by xing on 3/8/15.
 */
public interface ReviewRepository {

    // simple manipulatives

    Review save(Review review);

    Review saveOrUpdate(Review review);

    Review loadById(Long id);

    List<Review> loadAll();


    // using product

    List<Review> loadLatestByProduct(Product product, int pageNum, int pageSize);

    List<Review> loadAllByProduct(Product product);


    // using brand

    List<Review> loadLimitLatestLowestByBrand(Brand brand, int pageNum, int pageSize);

    List<Review> loadAllByBrand(Brand brand);


}
