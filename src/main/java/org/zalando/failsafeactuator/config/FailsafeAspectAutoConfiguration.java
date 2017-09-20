/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.config;

import com.fasterxml.jackson.core.Versioned;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.zalando.failsafeactuator.aspect.FailsafeBreakerAspect;
import org.zalando.failsafeactuator.aspect.FailsafeFallback;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;
import org.zalando.failsafeactuator.service.FallbackRegistry;

import java.lang.reflect.Method;

@EnableAspectJAutoProxy
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
@Slf4j
public class FailsafeAspectAutoConfiguration {

  @Bean
  public FailsafeBreakerAspect failsafeBreakerAspect(CircuitBreakerRegistry circuitBreakerRegistry) {
    return new FailsafeBreakerAspect(circuitBreakerRegistry, fallbackRegistry());
  }

  @Bean
  public Advisor failsafeBreakerPointcutAdvisor(FailsafeBreakerAspect failsafeBreakerAspect) {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("@annotation(org.zalando.failsafeactuator.aspect.Failsafe)");
    return new DefaultPointcutAdvisor(pointcut, failsafeBreakerAspect);
  }

  @Bean
  public FallbackRegistry fallbackRegistry() {
    return new FallbackRegistry();
  }
}
