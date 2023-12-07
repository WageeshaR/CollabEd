package com.collabed.core.data.dto;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final List<String> roles;
    private final InstitutionResponseDto institution;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.roles = user.getRoles().stream().map(Role::getAuthority).toList();
        this.institution = new InstitutionResponseDto(user.getInstitution());
    }
}
