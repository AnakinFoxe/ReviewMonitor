package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;

import java.util.List;

/**
 * Created by xing on 5/1/15.
 */
public interface BrandService {

    Brand loadById(Long id);

    List<Brand> loadAll();
}
