package com.collabed.core.data.model.location;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class Country {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String isoCode;
}
