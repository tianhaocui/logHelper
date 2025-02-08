package com.loghelper.configuration;

import com.loghelper.annotation.Monitor;
import com.loghelper.bytemonitor.MethodInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ByteMonitorConfiguration {
    @Bean
    public AgentBuilder byteMonitorAgent() {
        return new AgentBuilder.Default()
            .type(ElementMatchers.any())
            .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder.method(ElementMatchers.isAnnotatedWith(Monitor.class))
                .intercept(MethodDelegation.to(MethodInterceptor.class)));
    }
} 