package com.collabed.core.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Configuration
public class OAuth2Config extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${client.host}")
    private String clientHost;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(clientHost + "/home");

        // TODO: handle auth context setting and user roles, etc.

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
