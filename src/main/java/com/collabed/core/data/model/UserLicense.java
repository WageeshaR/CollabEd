package com.collabed.core.data.model;

import com.collabed.core.data.model.license.LicenseModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
public class UserLicense {
    @NotNull
    @JsonProperty("license_model")
    private LicenseModel licenseModel;
    @NotNull
    @JsonProperty("licensed_under")
    @DocumentReference
    private User licensedUnder;
}
