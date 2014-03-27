package org.jboss.resteasy.spring.aop;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ApplicationContextIT {


  @Test
  public void testContextCanBeCreated() {
    assertServiceCallResult("org/jboss/resteasy/spring/aop/context.xml", "testService", "test123", "test123");
  }

  @Test
  public void testContextCanBeCreatedForAnnotatedInteface() {
    assertServiceCallResult("org/jboss/resteasy/spring/aop/aop-context.xml", "testServiceJDKProxy",  AdviceImpl.RESUlT_REPLACEMENT, "test123");
    assertServiceCallResult("org/jboss/resteasy/spring/aop/aop-context.xml", "testServiceCGLIBProxy",  AdviceImpl.RESUlT_REPLACEMENT, "test123");
  }

  @Test
  public void testContextCanBeCreatedForAnnotatedImplementation() {
    assertServiceCallResult("org/jboss/resteasy/spring/aop/annotatedImplementation/applicationContext.xml", "testServiceJDKProxy",  AdviceImpl.RESUlT_REPLACEMENT, "test123");
    assertServiceCallResult("org/jboss/resteasy/spring/aop/annotatedImplementation/applicationContext.xml", "testServiceCGLIBProxy",  AdviceImpl.RESUlT_REPLACEMENT, "test123");
  }
  @Test
  public void testCanBeCreatedForAspectJ() {
    assertServiceCallResult("org/jboss/resteasy/spring/aop/aspectj/applicationContext.xml", "testService",  AdviceImpl.RESUlT_REPLACEMENT, "test123");
  }


  private void assertServiceCallResult(String contextFilename, String beanId, String expectedResult, String query) {
    AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { contextFilename });

    TestService service = (TestService) applicationContext.getBean(beanId);

    assertEquals(expectedResult, service.test(query));

    applicationContext.close();
  }

}
