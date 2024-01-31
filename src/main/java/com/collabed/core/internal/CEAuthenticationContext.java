package com.collabed.core.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@AllArgsConstructor
@Data
public class CEAuthenticationContext {
    private final CEGateway gateway;
    private final CEAuthentication authentication;

    public boolean validateToken() {
        return false;
    }
}
