package com.collabed.core.data.model.user.profile;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EducationLevelEnum {
    PHD("Doctoral Degree"),
    MASTER("Master's Degree"),
    BACHELORS("Bachelor's Degree"),
    DIPLOMA("Diploma"),
    OTHER("Other");

    private final String educationLevel;

    EducationLevelEnum(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    @JsonValue
    public String educationLevel() {
        return this.educationLevel;
    }
}
