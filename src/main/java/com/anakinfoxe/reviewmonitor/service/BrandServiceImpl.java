package com.anakinfoxe.reviewmonitor.service;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.repository.BrandRepository;
import com.anakinfoxe.reviewmonitor.repository.ProductRepository;
import com.anakinfoxe.reviewmonitor.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xing on 5/1/15.
 */
@Service("brandService")
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public Brand loadById(Long id) {
        return brandRepository.loadById(id);
    }

    @Override
    public List<Brand> loadAll() {
        return brandRepository.loadAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int deleteByName(String name) {
        Brand brand = brandRepository.loadByName(name);

        if (brand != null) {
            reviewRepository.deleteByBrand(brand);

            productRepository.deleteAllByBrand(brand);

            return brandRepository.deleteById(brand.getId());
        }

        return 0;
    }
}
