package com.collabed.core.runtime.exception;

public class CEUserErrorMessage {
    public static final String ENTITY_ALREADY_EXISTS = "A %s with the same primary key data already exists.";
    public static final String ENTITY_NOT_EXIST = "The specified %s does not exist in our system.";
    public static final String NO_MATCHING_ELEMENTS_FOUND = "Could not find any %s matching the criteria.";
    public static final String ENTITY_MUST_NOT_BE_NULL = "%s must not be null.";
    public static final String ENTITY_PROPERTY_MUST_NOT_BE_NULL = "%s %s must not be null.";
    public static final String ENTITY_NOT_BELONG_TO_USER = "Trying to update a %s not belong to current user.";
    public static final String GROUP_ROLE_NOT_MATCHED_WITH_USER = "User's role not matched with group's role.";
}
