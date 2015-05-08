package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;

import java.util.List;

/**
 * Created by xing on 5/2/15.
 */
public interface ProductService {

    List<Product> loadAllByBrand(Brand brand);
}
