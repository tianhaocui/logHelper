package com.loghelper.configuration;

import com.loghelper.annotation.Monitor;
import com.loghelper.bytemonitor.MethodInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.springframework.context.annotation.Bean;
import java.security.ProtectionDomain;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ByteMonitorConfiguration {
    @Bean
    public AgentBuilder byteMonitorAgent() {
        return new AgentBuilder.Default()
            .type(ElementMatchers.any())
            .transform(new AgentBuilder.Transformer() {
                @Override
                public DynamicType.Builder<?> transform(
                    DynamicType.Builder<?> builder,
                    TypeDescription typeDescription,
                    ClassLoader classLoader,
                    JavaModule module,
                    ProtectionDomain protectionDomain) {
                    
                    return builder.method(ElementMatchers.isAnnotatedWith(Monitor.class))
                        .intercept(MethodDelegation.to(MethodInterceptor.class));
                }
            });
    }
} 