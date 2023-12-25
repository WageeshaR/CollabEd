package com.collabed.core.data.model.channel;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@CompoundIndex(name = "name_topic", def = "{'name': 1, 'topic': 1}")
public class Channel {
    @Id
    private String id;
    private String name;
    private Topic topic;
    private String description;
}
