package org.jboss.resteasy.test.providers.sse;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.util.concurrent.ExecutorService;

/**
 * Created by kanovotn on 2/7/18.
 */
@Path("/sse")
public class SseWithoutWebXmlResource {

    @Context
    private ServletContext servletContext;

    @GET
    @Path("/domains")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void send(@Context SseEventSink eventSink, @Context Sse sse) {
        /*ExecutorService executor = (ExecutorService) servletContext
                .getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);*/
       // executor.execute(() -> {
            try (SseEventSink sink = eventSink) {
                eventSink.send(sse.newEvent("event1"));
                eventSink.send(sse.newEvent("event2"));
                eventSink.send(sse.newEvent("event3"));
            } catch (Exception ex) {
                System.out.println(ex.getStackTrace());
            }
       // });
    }
}
