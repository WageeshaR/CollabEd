package com.collabed.core.data.model.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class Price {
    @NotNull
    private String subject;
    @NotNull
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    private Currency currency = Currency.GBP;
}
