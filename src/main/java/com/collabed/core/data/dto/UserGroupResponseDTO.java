package com.collabed.core.data.dto;

import com.collabed.core.data.model.User;

import java.util.List;

public class UserGroupResponseDTO {
    private final String id;
    private final String name;
    private final List<User> users;

    public UserGroupResponseDTO(String id, String name, List<User> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }
}
