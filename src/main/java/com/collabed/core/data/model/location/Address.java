package com.collabed.core.data.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Address {
    @Id
    private String id;
    @NotNull
    @JsonProperty("line_1")
    private String line1;
    @JsonProperty("line_2")
    private String line2;
    @NotNull
    private String city;
    @NotNull
    @JsonProperty("postal_code")
    private String postalCode;
    private String county;
    @NotNull
    private Country country;
}
