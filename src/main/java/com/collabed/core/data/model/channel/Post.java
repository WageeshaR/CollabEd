package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@Data
public class Post {
    @Id
    private String id;
    @DocumentReference
    @NotNull
    private User author;
    private Post parent;
    private String title;
    @NotNull
    private PostContent content;
    @NotNull
    @DocumentReference
    private Channel channel;
}
