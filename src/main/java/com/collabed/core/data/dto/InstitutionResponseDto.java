package com.collabed.core.data.dto;

import com.collabed.core.data.model.Institution;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InstitutionResponseDto {
    private final String name;
    @JsonProperty("address_id")
    private final String addressId;

    public InstitutionResponseDto(Institution institution) {
        this.name = institution.getName();
        this.addressId = institution.getAddressId();
    }
}
