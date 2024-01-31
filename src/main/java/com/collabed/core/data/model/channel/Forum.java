package com.collabed.core.data.model.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Document
@Data
public class Forum {
    @Id
    private String id;
    @NotNull
    @DocumentReference
    private Channel channel;
}
