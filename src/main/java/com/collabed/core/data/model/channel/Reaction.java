package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
public class Reaction {
    String emoji;
    @NotNull
    @DocumentReference
    Post post;
    @DocumentReference
    User user;
}
