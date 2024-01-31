package com.collabed.core.data.model.location;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Data
@Document
@CompoundIndex(name = "name_iso_code", def = "{'name': 1, 'iso_code': 1}", unique = true)
public class Country {
    @Id
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String isoCode;
}
