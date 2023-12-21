package com.collabed.core.data.model.user.profile;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TitleEnum {
    CHANCELLOR("Chancellor"),
    VICE_CHANCELLOR("Vice-Chancellor"),
    HONORARY_PROFESSOR("Honorary Professor"),
    PROFESSOR("Professor"),
    DIRECTOR("Director"),
    DOCTOR("Doctor"),
    MR("Mr"),
    MS("Ms"),
    OTHER("Other");

    private final String title;

    TitleEnum(String title) {
        this.title = title;
    }

    @JsonValue
    public String title() {
        return this.title;
    }
}
