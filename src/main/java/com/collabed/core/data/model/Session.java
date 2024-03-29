package com.collabed.core.data.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class Session extends AuditMetadata {
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String sessionKey;
}
