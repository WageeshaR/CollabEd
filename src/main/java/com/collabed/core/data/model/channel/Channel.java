package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@CompoundIndex(name = "name_topic", def = "{'name': 1, 'topic': 1}")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Channel extends AuditMetadata {
    @Id
    private String id;
    @NotNull
    private String name;
    @DocumentReference
    @NotNull
    private Topic topic;
    private String description;
    private boolean deleted = false;
}
