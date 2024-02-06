package com.collabed.core.data.model.pricing;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Data
public class Price {
    @NotNull
    private String subject;
    @NotNull
    private BigDecimal unitPrice;
    private Currency currency = Currency.GBP;
}
