package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xing on 5/1/15.
 */
@Service("brandService")
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandRepository brandRepository;

    @Override
    public Brand loadById(Long id) {
        return brandRepository.loadById(id);
    }

    @Override
    public List<Brand> loadAll() {
        return brandRepository.loadAll();
    }
}
