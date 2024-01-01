package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.AuditMetadata;
import com.collabed.core.data.model.user.User;
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
public class Post extends AuditMetadata {
    @Id
    private String id;
    @DocumentReference
    private User author;
    private Post parent;
    private String title;
    @NotNull
    private PostContent content;
    private List<Reaction> reactions;
//    @NotNull
//    @DocumentReference
    private Channel channel;
}
