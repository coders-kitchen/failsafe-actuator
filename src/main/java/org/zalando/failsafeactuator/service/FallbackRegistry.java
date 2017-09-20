package org.zalando.failsafeactuator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.zalando.failsafeactuator.aspect.FallbackReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class FallbackRegistry {
  @Autowired
  private ApplicationContext applicationContext;

  private final Map<String, FallbackReference> fallbacks = new ConcurrentHashMap<>();

  public void register(String circuitBreakerName, String identifier, Method method, String beanName) {
    fallbacks.put(circuitBreakerName + "#" + identifier, new FallbackReference(method, beanName));
  }

  public boolean fallbackIsRegistered(String circuitBreakerName, String identifier) {
    return fallbacks.containsKey(circuitBreakerName + "#" + identifier);
  }

  public Object invokeFallback(String circuitBreakerName, String identifier, Object... parameters) throws Throwable {
    FallbackReference fallbackReference = fallbacks.get(circuitBreakerName + "#" + identifier);
    Method method = fallbackReference.getMethod();

    Object bean = applicationContext.getBean(fallbackReference.getBeanName());
    log.info("Breaker name {}, method {}, Object {}", circuitBreakerName, method.getName(), bean.getClass());

    try {
      return method.invoke(bean, parameters);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }


}
