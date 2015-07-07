package com.anakinfoxe.reviewmonitor.repository;

import com.anakinfoxe.reviewmonitor.model.Brand;

import java.util.List;

/**
 * Created by xing on 3/11/15.
 */
public interface BrandRepository {

    Brand save(Brand brand);

    Brand loadById(Long id);

    Brand loadByName(String name);

    List<Brand> loadAll();

    int deleteById(Long id);
}
