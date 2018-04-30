package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.tracing.resource.TracingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

@RunWith(Arquillian.class)
@RunAsClient
public class TracingTest {

    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(TracingTest.class.getSimpleName());
        war.addAsWebInfResource(TracingTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, TracingResource.class);
    }

    @Before
    public void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void close() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, TracingTest.class.getSimpleName());
    }

    @Test
    public void testTracingType() throws Exception {
        Response response = client.target(generateURL("/type")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("ALL", response.readEntity(String.class));
    }

    @Test
    public void testTracingLevel() throws Exception {
        Response response = client.target(generateURL("/level")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("TRACE", response.readEntity(String.class));
    }

    @Test
    public void testTracingLogger() throws Exception {
        Response response = client.target(generateURL("/logger")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }
}
