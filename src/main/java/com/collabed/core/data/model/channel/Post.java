package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.BatchProcessed;
import com.collabed.core.data.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Post extends BatchProcessed {
    @Id
    protected String id;
    @DocumentReference
    protected User author;
    protected Post parent;
    protected String title;
    @NotNull
    protected PostContent content;
    @NotNull
    @DocumentReference
    protected Channel channel;
}
