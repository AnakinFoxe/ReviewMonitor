package com.anakinfoxe.reviewmonitor.resource;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.model.Product;
import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.service.BrandService;
import com.anakinfoxe.reviewmonitor.service.ProductService;
import com.anakinfoxe.reviewmonitor.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xing on 5/1/15.
 */
@Path("/brand")
public class BrandResource {

    @Autowired
    BrandService brandService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Brand> getAll() {
        return brandService.loadAll();
    }


}
