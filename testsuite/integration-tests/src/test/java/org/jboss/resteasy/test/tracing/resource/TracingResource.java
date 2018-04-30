package org.jboss.resteasy.test.tracing.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.RESTEasyServerTracingEvent;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class TracingResource {
    @GET
    @Path("/type")
    public String type() {
        return ResteasyProviderFactory.getTracingType().toString();
    }

    @GET
    @Path("/level")
    public String level() {
        return ResteasyProviderFactory.getTracingThreshold().toString();
    }

    @GET
    @Path("/logger")
    public String logger() throws NoSuchMethodException {
        RESTEasyTracingLogger logger = ResteasyProviderFactory.getTracingLogger();
        logger.log(RESTEasyServerTracingEvent.METHOD_INVOKE, TracingResource.class.toString(),
                "logger()");
        return logger.toString();
    }
}
