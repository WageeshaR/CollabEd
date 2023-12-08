package com.collabed.core.service;

import com.collabed.core.data.dto.UserGroupResponseDto;
import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEUserServiceError;
import com.collabed.core.runtime.exception.CEErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserGroupRepository userGroupRepository) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
    }

    // users
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public List<User> getAll() { return userRepository.findAll(); }

    public List<User> getAll(String role) {
        return userRepository.findAllByRoles_Authority(role).orElseGet(List::of);
    }

    public UserResponseDto saveUser(User user, String role) {
        if (user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .noneMatch(r -> Objects.equals(r, role))) {
            return new UserResponseDto(userRepository.insert(user));
        } else {
            throw new CEUserServiceError(CEErrorMessage.ROLE_ALREADY_EXISTS);
        }
    }

    // user groups
    public UserGroup saveUserGroup(UserGroup group) {
        return userGroupRepository.insert(group);
    }

    public UserGroup addToGroup(String userId, String groupId) {
        if (userRepository.findById(userId).isEmpty()) throw new CEUserServiceError(CEErrorMessage.USER_NOT_EXIST);
        UserGroup userGroup = userGroupRepository.findById(groupId).orElseThrow();
        return userGroup.addToGroup(userId);
    }

    public UserGroupResponseDto loadGroupById(String id) {
        UserGroup group = userGroupRepository.findById(id).orElseThrow();
        List<UserResponseDto> usersOfGroup = new ArrayList<>();
        for (String userId : group.getUserIds()) {
            usersOfGroup.add(new UserResponseDto(userRepository.findById(userId).orElseThrow()));
        }
        return new UserGroupResponseDto(group.getId(), group.getName(), usersOfGroup);
    }
}
