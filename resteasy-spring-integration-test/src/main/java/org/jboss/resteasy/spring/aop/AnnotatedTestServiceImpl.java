package org.jboss.resteasy.spring.aop;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;



@Path("/aop/")
public class AnnotatedTestServiceImpl implements TestService {

  @Override
  @GET
  @Path(AnnotatedTestService.TEST_PATH)
  @Produces("text/plain")
  public String test(@QueryParam(AnnotatedTestService.QUERY_PARAM_NAME)String param) {
     return param;
  }
}
