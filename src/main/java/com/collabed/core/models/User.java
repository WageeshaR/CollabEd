package com.collabed.core.models;

public class User {
    private long id;
    private String uuid;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private InstitutionType institutionType;
    private Role role;
    private boolean has_consent_for_data_sharing;
    private boolean has_agreed_terms;
}
