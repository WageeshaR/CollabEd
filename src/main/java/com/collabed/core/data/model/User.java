package com.collabed.core.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Data
public class User {
    @Id
    private String id;
    private String first_name;
    private String last_name;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private Role role;
    private InstitutionType institution_type;
    private boolean has_consent_for_data_sharing;
    private boolean has_agreed_terms;
}
