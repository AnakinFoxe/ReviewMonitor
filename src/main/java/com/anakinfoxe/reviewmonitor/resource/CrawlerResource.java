package com.anakinfoxe.reviewmonitor.resource;

import com.anakinfoxe.reviewmonitor.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by xing on 3/3/15.
 */
@Path("/crawl")
public class CrawlerResource {

    @Autowired
    CrawlerService crawlerService;

    @Path("{brand}")
    @GET
    public String startCrawling(@PathParam("brand") String brand) {

        int numOfProducts = crawlerService.crawlBrand(brand);

        return brand + " crawling finished. " + numOfProducts + " products (with reviews) crawled";
    }
}
