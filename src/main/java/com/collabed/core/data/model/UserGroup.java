package com.collabed.core.data.model;

import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Document
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
        if (userRepository.findById(userId).get().getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> Objects.equals(a, this.role))) {
            this.userIds.add(userId);
            return this;
        }
        throw new CEWebRequestError(CEErrorMessage.GROUP_ROLE_NOT_MATCHED_WITH_USER);
    }
}
