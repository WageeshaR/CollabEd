package com.collabed.core.data.model.user;

import com.collabed.core.data.model.AuditMetadata;
import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.user.profile.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User extends AuditMetadata implements UserDetails {
    @Id
    private String id;
    @NotNull
    @Size(min = 6)
    @Indexed(unique = true, sparse = true)
    private String username;
    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = """
                    * at least one digit
                    * at least one lower-case character
                    * at least one upper-case character
                    * at least one special character (@#$%^&+=)
                    * at least 8 characters long
                    * at most zero whitespace allowed
                    """
    )
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Indexed(unique = true)
    private String email;
    private String phone;
    private List<Role> roles;
    @DocumentReference
    private Institution institution;
    @DocumentReference
    private Profile profile;
    @DocumentReference
    private UserLicense license;
    @DocumentReference
    private List<Channel> channels;
    private boolean hasConsentForDataSharing;
    private boolean hasAgreedTerms;
    @JsonIgnore
    private boolean accountNonExpired;
    @JsonIgnore
    private boolean accountNonLocked;
    @JsonIgnore
    private boolean credentialsNonExpired;
    @JsonIgnore
    private boolean enabled;
    @JsonIgnore
    private boolean deleted = false;

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

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public boolean isHasAgreedTerms() {
        return hasAgreedTerms;
    }
    @JsonProperty
    public void setHasAgreedTerms(boolean hasAgreedTerms) {
        this.hasAgreedTerms = hasAgreedTerms;
    }
    @JsonIgnore
    public boolean isHasConsentForDataSharing() {
        return hasConsentForDataSharing;
    }
    @JsonProperty
    public void setHasConsentForDataSharing(boolean hasConsentForDataSharing) {
        this.hasConsentForDataSharing = hasConsentForDataSharing;
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
