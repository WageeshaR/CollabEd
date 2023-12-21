package com.collabed.core.data.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Document
public class UserGroup {
    @Id
    private String id;
    @NotNull
    private String name;
    @NotNull(message = "role must be specified")
    private String role;
    @Size(min = 1, message = "a group must have at least one member")
    @DocumentReference
    private List<User> users;

    public void addUsers(User... users) {
        if (this.users == null)
            this.users = new ArrayList<>();
        this.users.addAll(Arrays.asList(users));
    }
}
