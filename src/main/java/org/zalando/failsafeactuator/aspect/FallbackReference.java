package org.zalando.failsafeactuator.aspect;

import lombok.Data;

import java.lang.reflect.Method;

@Data
  public class FallbackReference {
    private final Method method;
    private final String beanName;
  }