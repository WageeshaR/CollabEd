package com.collabed.core.data.model.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Topic(
    @Id
    String id,
    String key,
    String parentKey,
    @Indexed(unique = true)
    String name,
    int layer
) {}
