package com.collabed.core.data.model.institution;

import com.collabed.core.data.model.AuditMetadata;
import com.collabed.core.data.model.location.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Institution extends AuditMetadata {
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String name;
    @DocumentReference
    private Address address;
}
