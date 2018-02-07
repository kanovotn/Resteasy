package org.jboss.resteasy.test.providers.sse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@RunWith(Arquillian.class)
@RunAsClient
public class SseWithoutWebXml {

    private final static Logger logger = Logger.getLogger(SseTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseWithoutWebXml.class.getSimpleName());
        war.addAsWebInfResource("org/jboss/resteasy/test/providers/sse/web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, SseWithoutWebXmlApplication.class, SseWithoutWebXmlResource.class);
    }

    private String generateURL(String path)
    {
        return PortProviderUtil.generateURL(path, SseWithoutWebXml.class.getSimpleName());
    }

    @Test
    public void test() throws Exception {
        final List<String> results = new ArrayList<String>();
        final CountDownLatch latch = new CountDownLatch(6);
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = new ResteasyClientBuilder().connectionPoolSize(10).build();
        WebTarget target = client.target(generateURL("/service/sse/domains"));

        SseEventSource eventSource = SseEventSource.target(target).build();
        Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
        eventSource.register(event -> {
            results.add(event.readData());
            latch.countDown();
        }, ex -> {
            errors.incrementAndGet();
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        });
        eventSource.open();

        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assert.assertEquals(0, errors.get());
        Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        //Assert.assertTrue("3 SseInboundEvent expected", results.size() == 3);
        Assert.assertEquals("3 SseInboundEvent expected",3, results.size());
        Assert.assertTrue("Expect the last event is Done event, but it is :" + results.toArray(new String[]
                {})[5], results.toArray(new String[]
                {})[5].indexOf("event3") > -1);
        eventSource.close();
        client.close();
    }
}
