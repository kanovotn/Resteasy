package org.jboss.resteasy.spring.aop;




public class TestServiceImpl implements AnnotatedTestService {

  @Override
  public String test(String param) {
    return param;
  }
}
