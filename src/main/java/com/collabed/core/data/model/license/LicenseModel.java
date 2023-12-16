package com.collabed.core.data.model.license;

import com.collabed.core.data.model.pricing.Price;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document
public class LicenseModel {
    @Id
    private String id;
    @NotNull
    @JsonProperty("license_type")
    private LicenseType type;
    @NotNull
    private int frequency;
    @NotNull
    @DocumentReference
    private Price premium;
}
