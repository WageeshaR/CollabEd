package com.collabed.core.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document
@Data
public class User implements UserDetails {
    @Id
    private String id;
    @NotNull
    @Size(min = 6, message = "username must be at least 6 characters long")
    @Indexed(unique = true)
    private String username;
    @NotNull
    private String password;
    @NotNull
    @JsonProperty("first_name")
    private String firstName;
    @NotNull
    @JsonProperty("last_name")
    private String lastName;
    @NotNull
    @Indexed(unique = true)
    private String email;
    private String phone;
    private List<Role> roles;
    @JsonProperty("institution")
    @DocumentReference
    private Institution institution;
    private UserLicense license;
    @JsonProperty("has_consent_for_data_sharing")
    private boolean hasConsentForDataSharing;
    @JsonProperty("has_agreed_terms")
    private boolean hasAgreedTerms;
    @JsonProperty("account_non_expired")
    private boolean accountNonExpired;
    @JsonProperty("account_non_locked")
    private boolean accountNonLocked;
    @JsonProperty("credentials_non_expired")
    private boolean credentialsNonExpired;
    private boolean enabled;

    public User() {
        super();
        this.roles = new ArrayList<>();
    }
    public User(String username, String role) {
        this.username = username;
        this.roles = new ArrayList<>();
        this.addRole(role);
    }

    public void addRole(String role) {
        if (roles != null) {
            roles.add(new Role(role));
        } else {
            throw new UnsupportedOperationException();
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
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
