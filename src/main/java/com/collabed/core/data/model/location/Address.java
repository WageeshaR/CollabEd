package com.collabed.core.data.model.location;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Address {
    @Id
    private String id;
    private Country country;
}
