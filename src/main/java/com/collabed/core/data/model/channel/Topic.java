package com.collabed.core.data.model.channel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Topic(@Id String id, @Indexed(unique = true) String name) {}
