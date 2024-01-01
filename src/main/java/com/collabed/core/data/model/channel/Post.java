package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.AuditMetadata;
import com.collabed.core.data.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Post extends AuditMetadata {
    @Id
    protected String id;
    @DocumentReference
    protected User author;
    protected Post parent;
    protected String title;
    @NotNull
    protected PostContent content;
    protected List<Reaction> reactions;
    @NotNull
    @DocumentReference
    protected Channel channel;
}
