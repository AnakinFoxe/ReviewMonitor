package com.anakinfoxe.reviewmonitor.resource;

import com.anakinfoxe.reviewmonitor.model.Brand;
import com.anakinfoxe.reviewmonitor.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xing on 5/1/15.
 */
@Path("/brand")
public class BrandResource {

    @Autowired
    BrandService brandService;

    // TODO: might remove this from code
    private String key_ = "WhatAGoodDay!!";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Brand> getAll() {
        return brandService.loadAll();
    }

    @Path("{brand}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteBrand(@PathParam("brand") String brand,
                              @DefaultValue("") @QueryParam("key") String key) {
        if (!key.equals(key_))
            return "Key is either not present or not correct.";

        if (brandService.deleteByName(brand) != 0)
            return brand + " successfully deleted.";

        return brand + " deletion failed...";
    }


}
