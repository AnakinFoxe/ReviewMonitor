package com.anakinfoxe.reviewmonitor.resource;

import com.anakinfoxe.reviewmonitor.model.Review;
import com.anakinfoxe.reviewmonitor.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
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


    @Path("/replied/{reviewId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void changeReviewToReplied(@PathParam("reviewId") Long reviewId) {
        reviewService.changeReviewToReplied(reviewId);
    }
}
