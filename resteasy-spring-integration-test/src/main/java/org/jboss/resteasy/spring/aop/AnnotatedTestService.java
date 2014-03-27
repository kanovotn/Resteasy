package org.jboss.resteasy.spring.aop;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;



@Path("/aop/")
public interface AnnotatedTestService extends TestService {

  public static final String TEST_PATH = "test";
  public static final String QUERY_PARAM_NAME = "param";

  @Override
  @GET
  @Path(AnnotatedTestService.TEST_PATH)
  @Produces("text/plain")
  public String test(@QueryParam(AnnotatedTestService.QUERY_PARAM_NAME)String param);


}
