package org.jboss.resteasy.test.providers.sse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.category.Jaxrs21;
import org.jboss.resteasy.test.providers.sse.resource.SseEventSourceResource;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(Arquillian.class)
@RunAsClient
@Category(Jaxrs21.class)
public class SseEventSourceTest {
    private final static Logger logger = Logger.getLogger(SseEventSourceTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseEventSourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SseSmokeResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseEventSourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test `SseEventSource.register(Consumer<InboundSseEvent> onEvent)`
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testSseEventSourceOnEventCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/sse/events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                results.add(event);
                latch.countDown();
            });
            eventSource.open();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        }
    }

}
