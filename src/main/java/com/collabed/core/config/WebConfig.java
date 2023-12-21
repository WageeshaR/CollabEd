package com.collabed.core.config;

import com.collabed.core.api.util.CustomHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("${client.host}")
    private String clientHost;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(clientHost)
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.PUT.name())
                .exposedHeaders(CustomHttpHeaders.SESSION_KEY, "Authorization");
    }
}
