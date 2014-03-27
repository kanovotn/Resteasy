package org.jboss.resteasy.spring.basic;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class BasicIT
{
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
    WebArchive archive =  ShrinkWrap.create(ZipImporter.class,  DEPLOYMENT + ".war").importFrom(new File("target/" + DEPLOYMENT + ".war"))
        .as(WebArchive.class);

    return archive
        ;
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
  public void testBasicResourceGet() throws Exception {
    httpMethod = new GetMethod(CONTEXT_URL + "/" + SimpleResource.BASIC_PATH);

    assertResponse(HttpResponseCodes.SC_OK, "basic");
  }

  @Test
  public void testBasicResourcePut() throws Exception {
    httpMethod = new PutMethod(CONTEXT_URL + "/" + SimpleResource.BASIC_PATH);
    ((PutMethod)httpMethod).setRequestEntity(new StringRequestEntity("basic", "text/plain", null));

    assertResponse(HttpResponseCodes.SC_NO_CONTENT, null);
  }

  @Test
  public void testQueryParamResourceGet() throws Exception {
    String parameter = "hello world";
    httpMethod = new GetMethod(CONTEXT_URL + "/" + SimpleResource.QUERY_PARAM_PATH);
    httpMethod.setQueryString(new NameValuePair[]{new NameValuePair(SimpleResource.QUERY_PARAM_NAME, parameter)});

    assertResponse(HttpResponseCodes.SC_OK, parameter);
  }

  @Test
  public void testUriParamResourceGet() throws Exception {
    String uriParameter = "1234";
    httpMethod = new GetMethod(CONTEXT_URL + "/" + SimpleResource.URI_PARAM_PATH + "/" + uriParameter);

    assertResponse(HttpResponseCodes.SC_OK, uriParameter);
  }

  @Test
  public void testLocatingBasicResourceGet() throws Exception {
    httpMethod = new GetMethod(CONTEXT_URL + "/" + LocatingResource.PATH + "/" + SimpleResource.BASIC_PATH);

    assertResponse(HttpResponseCodes.SC_OK, "basic");
  }

  @Test
  public void testLocatingBasicResourcePut() throws Exception {
    httpMethod = new PutMethod(CONTEXT_URL + "/" + LocatingResource.PATH + "/" + SimpleResource.BASIC_PATH);
    ((PutMethod)httpMethod).setRequestEntity(new StringRequestEntity("basic", "text/plain", null));

    assertResponse(HttpResponseCodes.SC_NO_CONTENT, null);
  }

  @Test
  public void testLocatingQueryParamResourceGet() throws Exception {
    String parameter = "hello world";
    httpMethod = new GetMethod(CONTEXT_URL + "/" + LocatingResource.PATH + "/" + SimpleResource.QUERY_PARAM_PATH);
    httpMethod.setQueryString(new NameValuePair[]{new NameValuePair(SimpleResource.QUERY_PARAM_NAME, parameter)});

    assertResponse(HttpResponseCodes.SC_OK, parameter);
  }

  @Test
  public void testLocatingUriParamResourceGet() throws Exception {
    String uriParameter = "1234";
    httpMethod = new GetMethod(CONTEXT_URL + "/" + LocatingResource.PATH + "/" + SimpleResource.URI_PARAM_PATH + "/" + uriParameter);

    assertResponse(HttpResponseCodes.SC_OK, uriParameter);
  }

  protected void assertResponse(int expectedStatus, String expectedResponseBody) throws IOException, HttpException {
    assertEquals(expectedStatus, client.executeMethod(httpMethod));
    assertEquals(expectedResponseBody, httpMethod.getResponseBodyAsString());
  }

}
