package org.jboss.resteasy.test.client;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Tests client exception handling for AsyncInvoker interface and InvocationCallBack interface.
 */
public class AsyncInvokeExceptionsTest extends BaseResourceTest {

    @XmlRootElement
    public static class Sticker
    {
        private String name;

        public Sticker(String name) { this.name = name; }

        @XmlElement
        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }

    @Path("/")
    public static class Resource {

        @GET
        @Path("sticker")
        @Produces("application/xml")
        public Sticker get() throws InterruptedException {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Sticker("turtle");
        }

        @GET
        @Path("get")
        public Response get2() throws InterruptedException {
            Thread.sleep(10000);
            return Response.ok().build();
        }
    }



    static Client client;

    @BeforeClass
    public static void setupClient()
    {
        addPerRequestResource(Resource.class);
        client = ClientBuilder.newClient();

    }

    @AfterClass
    public static void close()
    {
        client.close();
    }

    public static class StickerCallback implements InvocationCallback<Sticker>
    {

        @Override
        public void completed(Sticker sticker) {
            System.out.println(sticker.getName());
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                System.out.println("GOOD: " + throwable.toString());
            } else {
                throwable.printStackTrace();
            }
        }
    }

    public static class ResponseCallback implements InvocationCallback<Response>
    {

        @Override
        public void completed(Response response) {
            System.out.println("OK");
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                System.out.println("GOOD: " + throwable.toString());
            } else {
                throwable.printStackTrace();
            }
        }
    }

    /*
     * Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     */
    //@Test(expected = TimeoutException.class)
    @Test
    public void futureTimeOutTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/") + "/sticker");
        Future<Sticker> future = base.request().async().get(Sticker.class);
        Sticker stickerName = future.get(5, TimeUnit.SECONDS);
    }

    //@Test(expected = TimeoutException.class)
    @Test
    public void futureTimeOutTest2() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/") + "/get");
        Future<Response> future = base.request().async().get();
        Response response = future.get(5, TimeUnit.SECONDS);
    }

    /*
     * Invocation callback should close all connections by itself
     */
    //@Test(expected = TimeoutException.class)
    public void invocationCallbackTimeoutTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/") + "/sticker");
        Future<Sticker> future = base.request().async().get(new StickerCallback());
        future.get(5, TimeUnit.SECONDS);
    }

    /*
     * Invocation callback should close all connections by itself
     */
    //@Test(expected = TimeoutException.class)
    public void invocationCallbackTimeoutTest2() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/") + "/get");
        Future<Response> future = base.request().async().get(new ResponseCallback());
        future.get(5, TimeUnit.SECONDS);
    }
}
