package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.AuditMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@CompoundIndex(name = "name_topic", def = "{'name': 1, 'topic': 1}")
public class Channel extends AuditMetadata {
    @Id
    private String id;
    private String name;
    private Topic topic;
    private String description;
}
