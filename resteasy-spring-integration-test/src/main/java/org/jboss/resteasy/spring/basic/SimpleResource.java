package org.jboss.resteasy.spring.basic;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class SimpleResource
{

   public static final String BASIC_PATH = "basic";
   public static final String QUERY_PARAM_PATH = "queryParam";
   public static final String URI_PARAM_PATH = "uriParam";
   public static final String MATRIX_PARAM_PATH = "matrixParam";
   public static final String QUERY_PARAM_NAME = "param";

   @GET
   @Path(BASIC_PATH)
   @Produces("text/plain")
   public String getBasic()
   {
      System.out.println("getBasic()");
      return "basic";
   }

   @PUT
   @Path(BASIC_PATH)
   @Consumes("text/plain")
   public void putBasic(String body)
   {
      System.out.println(body);
   }

   @GET
   @Path(QUERY_PARAM_PATH)
   @Produces("text/plain")
   public String getQueryParam(@QueryParam(QUERY_PARAM_NAME)String param)
   {
      return param;
   }

   @GET
   @Path(MATRIX_PARAM_PATH)
   @Produces("text/plain")
   public String getMatrixParam(@MatrixParam("param")String param)
   {
      return param;
   }

   @GET
   @Path(URI_PARAM_PATH + "/{param}")
   @Produces("text/plain")
   public int getUriParam(@PathParam("param")int param)
   {
      return param;
   }


}