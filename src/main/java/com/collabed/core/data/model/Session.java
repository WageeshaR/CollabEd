package com.collabed.core.data.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Session {
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String sessionKey;
}
