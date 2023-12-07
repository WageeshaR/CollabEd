package com.collabed.core.data.model.location;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Address {
    @Id
    private String id;
    private Country country;
}
