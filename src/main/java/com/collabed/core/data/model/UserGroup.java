package com.collabed.core.data.model;

import com.collabed.core.data.repository.user.UserRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class UserGroup {
    @Id
    private String id;
    private String name;
    @NotNull(message = "role must be specified")
    private String role;
    @Size(min = 1, message = "a group must have at least one member")
    private List<String> userIds;

    private final UserRepository userRepository;

    public UserGroup addToGroup(String userId) {
        if (this.userIds == null) {
            this.userIds = new ArrayList<>();
        }
        if (userRepository.findById(userId).get().getRoles().stream().map(Role::getAuthority).anyMatch(a -> Objects.equals(a, this.role))) {
            this.userIds.add(userId);
            return this;
        }
        throw new RuntimeException();
    }
}
