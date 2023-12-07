package com.collabed.core.data.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class Country {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    @JsonProperty("iso_code")
    private String isoCode;
}
