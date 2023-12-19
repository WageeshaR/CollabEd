package com.collabed.core.data.model.license;

import com.collabed.core.data.model.pricing.Price;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@CompoundIndex(
        name = "type_frequency_default_size_unit_count",
        def = "{'type': 1, 'frequency': 1, 'defaultUnitSize': 1, 'unitCount': 1}",
        unique = true
)
public class LicenseModel {
    @Id
    private String id;
    @NotNull
    @JsonProperty("license_type")
    private LicenseType type;
    @NotNull
    private int frequency;
    @NotNull
    @JsonProperty("default_unit_size")
    private int defaultUnitSize;
    @JsonProperty("unit_count")
    private int unitCount = 1;
    @NotNull
    private Price premium;

    @AssertTrue
    public boolean isTypeUnitValidated() {
        if (this.type == LicenseType.INDIVIDUAL)
            return this.unitCount == 1;
        return true;
    }
}
