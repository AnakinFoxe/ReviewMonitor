package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;

import java.util.List;

/**
 * Created by xing on 3/8/15.
 */
public interface ReviewRepository {

    Review save(Review review);

    Review saveOrUpdate(Review review);

    Review loadById(Long id);

    List<Review> loadAllByProduct(Product product);

    List<Review> loadAll();
}
