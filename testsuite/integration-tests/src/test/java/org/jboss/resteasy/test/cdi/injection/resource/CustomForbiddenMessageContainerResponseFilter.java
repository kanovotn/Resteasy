package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CustomForbiddenMessageContainerResponseFilter implements ContainerResponseFilter {

    private static Logger log = Logger.getLogger(CustomForbiddenMessageContainerResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        containerResponseContext.getStatus();
        if (containerResponseContext.hasEntity()) {
            String message = (String) containerResponseContext.getEntity();
            log.info("My custom ContainerResponseFilter: " + message);
        }
    }
}
