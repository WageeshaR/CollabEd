package com.collabed.core.data.model.pricing;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document
public class Price {
    @Id
    private String id;
    @NotNull
    private String subject;
    @NotNull
    private BigDecimal value;
    private Currency currency = Currency.GBP;
}
