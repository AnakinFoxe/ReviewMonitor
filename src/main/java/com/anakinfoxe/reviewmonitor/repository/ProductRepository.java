package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Product;

import java.util.List;

/**
 * Created by xing on 3/8/15.
 */
public interface ProductRepository {

    Product save(Product product);

    Product saveOrUpdate(Product product);

    Product loadById(Long id);

    Product loadByProductId(String productId);

    List<Product> loadAll();
}
