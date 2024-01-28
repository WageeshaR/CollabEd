package com.collabed.core.config;

import com.collabed.core.internal.SimpleIntelGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class GatewayBeanConfig {

    private final String intelGatewayHostUrl;

    public GatewayBeanConfig(@Value("${intel-gateway.host}") String hostUri) {
        this.intelGatewayHostUrl = hostUri;
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SimpleIntelGateway requestScopedIntelGateway() {
        return new SimpleIntelGateway(intelGatewayHostUrl);
    }
}
