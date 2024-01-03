package com.collabed.core.config;

import com.collabed.core.internal.SimpleIntelGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class GatewayBeanConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
    public SimpleIntelGateway requestScopedIntelGateway() {
        return new SimpleIntelGateway();
    }
}
