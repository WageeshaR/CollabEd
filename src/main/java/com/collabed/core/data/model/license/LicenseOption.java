package com.collabed.core.data.model.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LicenseOption {
    @NotNull
    private String id;
    @NotNull
    @JsonProperty("license_model")
    private LicenseModel model;
}
