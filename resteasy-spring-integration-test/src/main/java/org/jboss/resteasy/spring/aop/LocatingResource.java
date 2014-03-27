package org.jboss.resteasy.spring.aop;

import java.lang.annotation.Annotation;

import javax.ws.rs.Path;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.StringUtils;

@Path("/aop/")
public class LocatingResource {
  public static final String TEST_PATH = "test";
  public static final String TEST_CGLIB_PROXY_PATH = "testCGLIB";
  public static final String TEST_JDK_PROXY_PATH = "testJdk";


  private final TestService service;
  private final TestService cglibProxyService;
  private final TestService jdkProxyService;

  public LocatingResource(TestService service, TestService cglibProxyService, TestService jdkProxyService) {
    this.service = service;
    this.cglibProxyService = cglibProxyService;
    this.jdkProxyService = jdkProxyService;

  }

  @Path(TEST_PATH)
  public TestService test() {
    String annotations = getAnnotations(service);
    System.out.println("LOCATING service ... service result: " + service.test("no proxy") + " service class " + service.getClass().getName() + " service annotations " + annotations);
    return service;
  }


  private String getAnnotations(Object object) {
    Object unwrapped = object;
    try {
      unwrapped = AspectUtils.unwrapProxy(object);
//      ((YourHandler)Proxy.getInvocationHandler(object)).getOriginalObject();
    } catch (Exception e) {
      System.err.println("getAnnotations error " + e.getMessage());
    }
    if (unwrapped.getClass() != object.getClass()) {
      System.out.println("Found proxy " + unwrapped.getClass().getName() + " annotations " + getAnnotations(unwrapped));
    }
    StringBuilder result = new StringBuilder();
    Class<?> clazz = object.getClass();
    Annotation[] classAnnotations = clazz.getAnnotations();
    result.append(clazz.getName() + "[" + StringUtils.arrayToCommaDelimitedString(classAnnotations) + "]");
    Class<?> [] interfaces = clazz.getInterfaces();
    result.append(" intefaces [");
    for (Class<?> class1 : interfaces) {
      classAnnotations = class1.getAnnotations();
      result.append(class1.getName() + "[" + StringUtils.arrayToCommaDelimitedString(classAnnotations) + "]");
    }
    result.append("]");
    return result.toString();
  }

  @Path(TEST_CGLIB_PROXY_PATH)
  public TestService testCGLIB() {
    String annotations = getAnnotations(cglibProxyService);
    System.out.println("LOCATING service ... service result: " + cglibProxyService.test("cglib") + " service class " + cglibProxyService.getClass().getName() + " service annotations " + annotations);
    return cglibProxyService;
  }

  @Path(TEST_JDK_PROXY_PATH)
  public TestService testJdk() {
    String annotations = getAnnotations(jdkProxyService);
    System.out.println("LOCATING service ... service result: " + jdkProxyService.test("jdk") + " service class " + jdkProxyService.getClass().getName() + " service annotations " + annotations);
    return jdkProxyService;
  }

  static

  /**
   * This is a utility class for getting raw objects that may have been proxied.
   * It is intended to be used in cases where raw implementations are needed rather
   * than working with interfaces which they implement.
   * see http://forum.spring.io/forum/spring-projects/aop/52011-need-to-unwrap-a-proxy-to-get-the-object-being-proxied
   * see http://jira.codehaus.org/browse/XFIRE-218
   */
  public final class AspectUtils {

    public static final Object unwrapProxy(Object bean) throws Exception {

      /*
       * If the given object is a proxy, set the return value as the object
       * being proxied, otherwise return the given object.
       */
      if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {

        return ((Advised) bean).getTargetSource().getTarget();
      }

   /* FIXME if (AopUtils.isJdkDynamicProxy(bean)) {
        return ?
      */
      return bean;
    }

  }
}