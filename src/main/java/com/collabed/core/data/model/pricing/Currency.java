package com.collabed.core.data.model.pricing;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    GBP("British Pound Sterling"),
    USD("United States Dollar"),
    EUR("Euro");

    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    @JsonValue
    private String currency() {
        return this.currency;
    }
}
