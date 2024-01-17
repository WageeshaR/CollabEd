package com.collabed.core.service.util;

import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static Authentication withAuthentication() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication auth = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(context);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        return auth;
    }
}
