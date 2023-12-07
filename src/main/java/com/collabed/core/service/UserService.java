package com.collabed.core.service;

import com.collabed.core.data.dto.UserGroupResponseDTO;
import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow();
    }

    public List<User> getAll() { return userRepository.findAll(); }

    public List<User> getAll(String role) {
        return userRepository.findAllByRoles_Authority(role).orElseGet(List::of);
    }

    public User registerStudent(User user) {
        user.addRole("ROLE_STUDENT");
        return userRepository.insert(user);
    }

    public User registerFacilitator(User user) {
        user.addRole("ROLE_FACILITATOR");
        return userRepository.insert(user);
    }

    public User registerAdmin(User user) {
        user.addRole("ROLE_ADMIN");
        return userRepository.insert(user);
    }

    // user groups
    public UserGroup createUserGroup(UserGroup group) {
        return userGroupRepository.insert(group);
    }

    public UserGroup addToGroup(String userId, String groupId) {
        if (userRepository.findById(userId).isEmpty()) throw new RuntimeException();
        UserGroup userGroup = userGroupRepository.findById(groupId).orElseThrow();
        return userGroup.addToGroup(userId);
    }

    public UserGroupResponseDTO loadGroupById(String id) {
        UserGroup group = userGroupRepository.findById(id).orElseThrow();
        List<User> usersOfGroup = new ArrayList<>();
        for (String userId : group.getUserIds()) {
            usersOfGroup.add(userRepository.findById(userId).orElseThrow());
        }
        return new UserGroupResponseDTO(group.getId(), group.getName(), usersOfGroup);
    }
}
