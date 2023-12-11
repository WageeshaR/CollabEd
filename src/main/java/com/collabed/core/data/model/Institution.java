package com.collabed.core.data.model;

import com.collabed.core.data.model.location.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Institution {
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String name;
    @JsonProperty("address_id")
    private String addressId;
    @Transient
    private Address address;
}
