package com.collabed.core.data.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document
@CompoundIndex(
        name = "line1_city_postal_code",
        def = "{'line_1': 1, 'city': 1, 'postal_code': 1}",
        unique = true
)
public class Address {
    @Id
    private String id;
    @NotNull
    private String line1;
    private String line2;
    @NotNull
    private String city;
    @NotNull
    private String postalCode;
    private String county;
    @NotNull
    @DocumentReference
    private Country country;
}
