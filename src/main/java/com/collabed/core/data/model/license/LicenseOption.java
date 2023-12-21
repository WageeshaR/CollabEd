package com.collabed.core.data.model.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
public class LicenseOption {
    @NotNull
    private String id;
    @NotNull
    private LicenseModel model;
}
