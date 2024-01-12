package com.collabed.core.data.model.license;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LicenseOption {
    @NotNull
    private String id;
    @NotNull
    private LicenseModel model;
}
