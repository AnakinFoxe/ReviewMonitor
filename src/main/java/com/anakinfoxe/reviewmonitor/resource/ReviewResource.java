package com.anakinfoxe.reviewmonitor.resource;

import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xing on 3/3/15.
 */
@Path("/review")
public class ReviewResource {

    @Autowired
    ReviewService reviewService;

    @Path("/brand/{brandId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Review> getAll(@PathParam("brandId") Long brandId) {
        return reviewService.loadAllByBrandId(brandId);
    }
}
