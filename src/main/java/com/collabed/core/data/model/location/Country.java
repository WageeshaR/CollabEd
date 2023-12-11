package com.collabed.core.data.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@CompoundIndex(name = "name_iso_code", def = "{'name': 1, 'iso_code': 1}", unique = true)
public class Country {
    @Id
    private String id;
    @NotNull
    private String name;
    @JsonProperty("iso_code")
    @NotNull
    private String isoCode;
}
