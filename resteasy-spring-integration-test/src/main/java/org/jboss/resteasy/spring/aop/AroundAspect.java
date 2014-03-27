package org.jboss.resteasy.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AroundAspect {

  @Around("execution(* com.mkyong.customer.bo.CustomerBo.addCustomerAround(..))")
  public Object methodAround(ProceedingJoinPoint joinPoint) throws Throwable {
    return AdviceImpl.RESUlT_REPLACEMENT;
  }

}