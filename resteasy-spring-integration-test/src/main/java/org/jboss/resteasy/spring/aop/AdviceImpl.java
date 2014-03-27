package org.jboss.resteasy.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AdviceImpl implements MethodInterceptor {
  static final String RESUlT_REPLACEMENT = "replaced return";

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    return RESUlT_REPLACEMENT;
  }
}
