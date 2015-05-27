package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SyncInvoketest extends BaseResourceTest
{

   @java.lang.annotation.Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @HttpMethod("PATCH")
   public @interface PATCH
   {
   }

   @Path("/test")
   public static class Resource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "get";
      }

      @PUT
      @Consumes("text/plain")
      public String put(String str)
      {
         return "put " + str;
      }

      @POST
      @Consumes("text/plain")
      public String post(String str)
      {
         return "post " + str;
      }

      @DELETE
      @Produces("text/plain")
      public String delete()
      {
         return "delete";
      }

      @PATCH
      @Produces("text/plain")
      @Consumes("text/plain")
      public String patch(String str)
      {
         return "patch " + str;
      }
   }

   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      client = ClientBuilder.newClient();
      addPerRequestResource(Resource.class);
   }

   @AfterClass
   public static void close()
   {
      client.close();
   }

   @Test
   public void testMethods() throws Exception
   {
      {
         Response res = client.target(generateURL("/test")).request().get();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("get", entity);

      }

      {
         String entity = client.target(generateURL("/test")).request().get(String.class);
         Assert.assertEquals("get", entity);

      }

      {
         Response res = client.target(generateURL("/test")).request().delete();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("delete", entity);

      }

      {
         String entity = client.target(generateURL("/test")).request().delete(String.class);
         Assert.assertEquals("delete", entity);

      }
      {
          Response res = client.target(generateURL("/test")).request().put(Entity.text("hello"));
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("put hello", entity);

       }
      {
         String entity = client.target(generateURL("/test")).request().put(Entity.text("hello"), String.class);
         Assert.assertEquals("put hello", entity);

      }

      {
          Response res = client.target(generateURL("/test")).request().post(Entity.text("hello"));
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("post hello", entity);

       }
      {
         String entity = client.target(generateURL("/test")).request().post(Entity.text("hello"), String.class);
         Assert.assertEquals("post hello", entity);

      }

      {
          Response res = client.target(generateURL("/test")).request().method("PATCH", Entity.text("hello"));
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("patch hello", entity);

       }
      {
         String entity = client.target(generateURL("/test")).request().method("PATCH", Entity.text("hello"), String.class);
         Assert.assertEquals("patch hello", entity);

      }
   }

   @Test
   public void testInvoke() throws Exception
   {
      {
         Response res = client.target(generateURL("/test")).request().buildGet().invoke();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("get", entity);

      }

      {
         String entity = client.target(generateURL("/test")).request().buildGet().invoke(String.class);
         Assert.assertEquals("get", entity);

      }

      {
         Response res = client.target(generateURL("/test")).request().buildDelete().invoke();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("delete", entity);

      }

      {
         String entity = client.target(generateURL("/test")).request().buildDelete().invoke(String.class);
         Assert.assertEquals("delete", entity);

      }
      {
          Response res = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).invoke();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("put hello", entity);

       }
      {
         String entity = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).invoke(String.class);
         Assert.assertEquals("put hello", entity);

      }

      {
          Response res = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).invoke();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("post hello", entity);

       }
      {
         String entity = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).invoke(String.class);
         Assert.assertEquals("post hello", entity);

      }

      {
          Response res = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).invoke();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("patch hello", entity);

       }
      {
         String entity = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).invoke(String.class);
         Assert.assertEquals("patch hello", entity);

      }
   }
}
