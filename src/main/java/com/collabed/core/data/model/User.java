package com.collabed.core.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private Role role;
    private InstitutionType institution_type;
    private boolean has_consent_for_data_sharing;
    private boolean has_agreed_terms;

    public User() {
        super();
    }
    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
