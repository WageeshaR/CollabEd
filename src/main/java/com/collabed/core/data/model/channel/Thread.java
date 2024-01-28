package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document
@Data
public class Thread {
    @Id
    private String id;
    @DocumentReference
    @Size(min = 2)
    private List<User> members;
    @NotNull
    private String subject;
    private boolean resolved = false;
}
