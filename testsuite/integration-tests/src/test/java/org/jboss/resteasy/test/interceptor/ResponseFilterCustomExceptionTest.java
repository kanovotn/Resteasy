package org.jboss.resteasy.test.interceptor;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.interceptor.resource.ResponseFilterCustomExceptionCustomException;
import org.jboss.resteasy.test.interceptor.resource.ThrowCustomExceptionResponseFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.21
 * @tpTestCaseDetails Throw custom exception from a ClientResponseFilter [RESTEASY-1591]
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseFilterCustomExceptionTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseFilterCustomExceptionTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addClasses(ResponseFilterCustomExceptionCustomException.class);
        return TestUtil.finishContainerPrepare(war, null, ThrowCustomExceptionResponseFilter.class);
    }

    static Client client;

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @After
    public void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseFilterCustomExceptionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Use ClientResponseFilter
     * @tpSince RESTEasy 3.0.23
     */
    @Test
    public void testThrowCustomException() throws Exception {
        client.register(ThrowCustomExceptionResponseFilter.class);
        try {
            client.target(generateURL("/testCustomException")).request().post(Entity.text("testCustomException"));
        } catch (ResponseProcessingException ex) {
            Assert.assertTrue(ex instanceof ResponseProcessingException);
            Assert.assertEquals(ResponseFilterCustomExceptionCustomException.class.getCanonicalName() + ": custom message", ex.getMessage());
            return;
        }
        Assert.fail("The exception thrown by client was not instance of javax.ws.rs.client.ResponseProcessingException");
    }
}
