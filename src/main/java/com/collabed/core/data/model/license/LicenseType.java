package com.collabed.core.data.model.license;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public enum LicenseType {
    INDIVIDUAL("individual"),
    GROUP("group"),
    INSTITUTIONAL("institutional");

    private final String type;

    LicenseType(String type) {
        this.type = type;
    }

    @JsonValue
    public String type() {
        return this.type;
    }
}
