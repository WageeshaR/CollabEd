package com.collabed.core.data.dto;

import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.User;
import com.collabed.core.data.util.ReferenceDataObjectMapper;
import com.collabed.core.runtime.exception.CEReferenceObjectMappingError;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

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
        this.roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        this.institution = user.getInstitution() != null ? institution(user.getInstitution()) : null;
    }

    private InstitutionResponseDto institution(Institution institution) throws CEReferenceObjectMappingError {
        ReferenceDataObjectMapper<Institution> mapper = new ReferenceDataObjectMapper<>();
        Institution referenceObject = mapper.readReferenceObject(institution, "InstitutionRepository");
        return new InstitutionResponseDto(referenceObject);
    }
}
