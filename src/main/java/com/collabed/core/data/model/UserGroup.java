package com.collabed.core.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    @JsonProperty("user_ids")
    private List<String> userIds;

    public void addUsers(String... ids) {
        if (this.userIds == null)
            this.userIds = new ArrayList<>();
        this.userIds.addAll(Arrays.asList(ids));
    }
}
