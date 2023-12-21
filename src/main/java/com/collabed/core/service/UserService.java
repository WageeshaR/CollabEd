package com.collabed.core.service;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEReferenceObjectMappingError;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
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

    public User saveUser(User user, String role) {
        if (user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .noneMatch(r -> Objects.equals(r, role))) {
            if (!Objects.equals(role, "ROLE_ADMIN") && user.getInstitution() == null){
                throw new CEWebRequestError(CEUserErrorMessage.INSTITUTION_NOT_NULL);
            }
            user.addRole(role);
            try {
                return userRepository.insert(user);
            } catch (CEReferenceObjectMappingError e) {
                throw new CEWebRequestError("Error resolving user data from object mapper");
            }
        } else {
            throw new CEWebRequestError(CEUserErrorMessage.ROLE_ALREADY_EXISTS);
        }
    }

    public void deleteUser(User user) {
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new CEServiceError("Error deleting the user: " + e.getMessage());
        }
    }

    // user groups
    public UserGroup saveUserGroup(UserGroup group) {
        return userGroupRepository.insert(group);
    }

    public UserGroup addToGroup(String userId, String groupId) {
        if (userRepository.findById(userId).isEmpty()) throw new CEWebRequestError(CEUserErrorMessage.USER_NOT_EXIST);
        UserGroup userGroup = userGroupRepository.findById(groupId).orElseThrow();
        List<Role> userRoles = userRepository.findById(userId).get().getRoles();
        if (userRoles.stream().noneMatch(r -> r.getAuthority() != null && r.getAuthority().equals(userGroup.getRole())))
            throw new CEWebRequestError(CEUserErrorMessage.GROUP_ROLE_NOT_MATCHED_WITH_USER);
        userGroup.addUsers(userId);
        return userGroup;
    }

    public UserGroup loadGroupById(String id) {
        return userGroupRepository.findById(id).orElseThrow();
    }
}
