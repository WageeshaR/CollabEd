package com.collabed.core.data.model.user;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public class Role implements GrantedAuthority {
    private final String authority;

    public Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
