package com.collabed.core.service;

import com.collabed.core.data.model.user.Role;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.UserGroup;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.data.repository.user.ProfileRepository;
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

import java.util.List;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final ProfileRepository profileRepository;

    public UserService(UserRepository userRepository,
                       UserGroupRepository userGroupRepository,
                       ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.profileRepository = profileRepository;
    }

    // users
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public User findUser(String id) {
        return userRepository.findById(id).orElseThrow();
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
                if (user.getProfile() != null) {
                    Profile userProfile = user.getProfile();
                    profileRepository.save(userProfile);
                }
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
        User user = userRepository.findById(userId).get();
        List<Role> userRoles = user.getRoles();
        if (userRoles.stream().noneMatch(r -> r.getAuthority() != null && r.getAuthority().equals(userGroup.getRole())))
            throw new CEWebRequestError(CEUserErrorMessage.GROUP_ROLE_NOT_MATCHED_WITH_USER);
        userGroup.addUsers(user);
        return userGroup;
    }

    public UserGroup loadGroupById(String id) {
        return userGroupRepository.findById(id).orElseThrow();
    }
}
