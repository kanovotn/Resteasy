package org.jboss.resteasy.spring.aop;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * see http://stackoverflow.com/questions/16686912/spring-recommendations-proxying-mechanism-vs-transactional-on-class-or-interfa
 *
 *  Spring recommends that you only annotate concrete classes (and methods of concrete classes) with the @Transactional annotation, as opposed to annotating interfaces. You certainly can place the @Transactional annotation on an interface (or an interface method), but this works only as you would expect it to if you are using interface-based proxies. The fact that Java annotations are not inherited from interfaces means that if you are using class-based proxies (proxy-target-class="true") or the weaving-based aspect (mode="aspectj"), then the transaction settings are not recognized by the proxying and weaving infrastructure, and the object will not be wrapped in a transactional proxy, which would be decidedly bad.
 *  (from http://static.springsource.org/spring/docs/3.0.x/reference/transaction.html)
 *
 *  Spring AOP uses either JDK dynamic proxies or CGLIB to create the proxy for a given target object. (JDK dynamic proxies are preferred whenever you have a choice).
 *  (from http://static.springsource.org/spring/docs/3.0.x/reference/aop.html#aop-understanding-aop-proxies)
 *
 *  in order to follow both recommendations, I need to have @Transactional annotation on concrete class, but still provide an interfaces (that this class implements) containing all transactional methods, so that Spring can use JDK dynamix proxies for this interface
 *
 *   If you use Compile Time AspectJ, then the Aspects get taken in accout even if you invoke a method from inside the same bean (this.otherMethod()).
 */
@RunWith(Arquillian.class)
@RunAsClient
@Ignore
public class AspectJIT {
  private static final String DEPLOYMENT = "resteasy-spring-integration-test";

  @Deployment
  public static WebArchive getDeployment() {

    /*
    File[] runtimeDependencies = Maven.resolver().resolve().withTransitivity().asFile();

    WebArchive archive = ShrinkWrap.create(WebArchive.class,  DEPLOYMENT + ".war")
        .addClass(SimpleResource.class)
        .addClass(LocatingResource.class)
        .addAsLibraries(runtimeDependencies)
        .addAsWebResource(new File("src/main/webapp/index.jsp"))
        .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
        .addAsWebInfResource(new File("src/main/webapp/WEB-INF/applicationContext.xml"))
        ;
    archive.as(ZipExporter.class).exportTo(new File(DEPLOYMENT + ".war"), true);
    */
    WebArchive archive = ShrinkWrap.create(ZipImporter.class, DEPLOYMENT + ".war")
        .importFrom(new File("target/" + DEPLOYMENT + ".war")).as(WebArchive.class);
    archive.addAsWebInfResource(new File("src/test/resources/" + "org.jboss.resteasy.spring.aop".replace(".", "/") + "/aspectj/applicationContext.xml"));
    return archive;
  }

  private static final String CONTEXT_URL = "http://localhost:8080/" + DEPLOYMENT;

  private HttpClient client;

  private HttpMethod httpMethod;

  @Before
  public void before() throws Exception {
    client = new HttpClient();
  }

  @After
  public void after() {
    if (httpMethod != null)
      httpMethod.releaseConnection();
  }

  @Test
  public void testQueryParamResourceGet() throws Exception {
    String parameter = "hello world";
    httpMethod = new GetMethod(CONTEXT_URL + "/aop/" + AnnotatedTestService.TEST_PATH);
    httpMethod.setQueryString(new NameValuePair[]{new NameValuePair(AnnotatedTestService.QUERY_PARAM_NAME, parameter)});

    assertResponse(HttpResponseCodes.SC_OK, AdviceImpl.RESUlT_REPLACEMENT);
  }

  @Test
  public void testLocatingQueryService() throws Exception {
    String parameter = "hello world2";
    httpMethod = new GetMethod(CONTEXT_URL + "/aop/" + LocatingResource.TEST_PATH + "/" + AnnotatedTestService.TEST_PATH);
    httpMethod.setQueryString(new NameValuePair[]{new NameValuePair(AnnotatedTestService.QUERY_PARAM_NAME, parameter)});

    assertResponse(HttpResponseCodes.SC_OK, parameter);
  }

  @Test
  public void testLocatingQueryCGLIBProxyService() throws Exception {
    String parameter = "hello world3";
    httpMethod = new GetMethod(CONTEXT_URL + "/aop/" + LocatingResource.TEST_CGLIB_PROXY_PATH + "/" + AnnotatedTestService.TEST_PATH);
    httpMethod.setQueryString(new NameValuePair[]{new NameValuePair(AnnotatedTestService.QUERY_PARAM_NAME, parameter)});

    assertResponse(HttpResponseCodes.SC_OK, AdviceImpl.RESUlT_REPLACEMENT);
  }

  @Test
  public void testLocatingQueryJdkProxyService() throws Exception {
    String parameter = "hello world4";
    httpMethod = new GetMethod(CONTEXT_URL + "/aop/" + LocatingResource.TEST_JDK_PROXY_PATH + "/" + AnnotatedTestService.TEST_PATH);
    httpMethod.setQueryString(new NameValuePair[]{new NameValuePair(AnnotatedTestService.QUERY_PARAM_NAME, parameter)});

    assertResponse(HttpResponseCodes.SC_OK, AdviceImpl.RESUlT_REPLACEMENT);
  }

  protected void assertResponse(int expectedStatus, String expectedResponseBody) throws IOException, HttpException {
    assertEquals(expectedStatus, client.executeMethod(httpMethod));
    assertEquals(expectedResponseBody, httpMethod.getResponseBodyAsString());
  }

}
