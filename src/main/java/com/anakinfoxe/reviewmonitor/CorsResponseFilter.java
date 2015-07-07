package com.anakinfoxe.reviewmonitor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

/**
 * Created by xing on 7/6/15.
 */
public class CorsResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext,
                       ContainerResponseContext containerResponseContext)
            throws IOException {
        containerResponseContext.getHeaders().add("Access-Control-Allow-Origin","*");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    }
}
