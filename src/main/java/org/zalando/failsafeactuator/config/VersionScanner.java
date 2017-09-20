package org.zalando.failsafeactuator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.zalando.failsafeactuator.aspect.FailsafeFallback;
import org.zalando.failsafeactuator.service.FallbackRegistry;

import java.lang.reflect.Method;

@Configuration
@Slf4j
public class VersionScanner implements ApplicationContextAware {

  @Autowired
  private FallbackRegistry fallbackRegistry;

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    for (String beanName : applicationContext.getBeanDefinitionNames()) {
      try {
        Object obj = applicationContext.getBean(beanName);
        Class<?> objClz = obj.getClass();
        if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {

          objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
        }

        for (Method m : objClz.getDeclaredMethods()) {
          if (m.isAnnotationPresent(FailsafeFallback.class)) {
            FailsafeFallback failsafeFallback = m.getAnnotation(FailsafeFallback.class);
            fallbackRegistry.register(failsafeFallback.value(), failsafeFallback.identifier(), m, beanName);
          }
        }
      } catch (Exception e) {
        log.debug("", e);
      }
    }
  }
}