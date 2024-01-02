package com.collabed.core.service;

import com.collabed.core.data.model.user.Role;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.UserGroup;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.data.repository.user.ProfileRepository;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Log4j2
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

    public CEServiceResponse findUser(String id) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            return CEServiceResponse.success().data(user);
        } catch (NoSuchElementException e) {
            log.error(String.format("User %s not found in database: %s", id, e));
            return CEServiceResponse.error().data(String.format("User %s not found in database.", id));
        }
    }

    public List<User> getAll() { return userRepository.findAll(); }

    public List<User> getAll(String role) {
        return userRepository.findAllByAuthority(role).orElseGet(List::of);
    }

    public CEServiceResponse saveUser(User user, String role) {
        try {
            if (user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .noneMatch(r -> Objects.equals(r, role)))
            {
                if (!Objects.equals(role, "ROLE_ADMIN") && user.getInstitution() == null) {
                    log.info(String.format("Provided user %s 's institution is null", user.getId()));
                    return CEServiceResponse
                            .error()
                            .data(String.format(CEUserErrorMessage.ENTITY_MUST_NOT_BE_NULL, "Institution"));
                }
                user.addRole(role);
                if (user.getProfile() != null) {
                    Profile userProfile = user.getProfile();
                    profileRepository.save(userProfile);
                }
                User savedUser = userRepository.insert(user);
                log.info(String.format("User %s is saved successfully", user.getUsername()));
                return CEServiceResponse.success().data(savedUser);
            } else {
                log.info(String.format("Specified role %s already exists in the user %s", role, user.getId()));
                return CEServiceResponse
                        .error()
                        .data(String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "role"));
            }
        } catch (RuntimeException e) {
            log.error(e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse deleteUser(String id) {
        try {
            userRepository.updateAndSoftDelete(id);
            return CEServiceResponse.success().build();
        } catch (Exception e) {
            log.error("Error deleting the user: " + e);
            return CEServiceResponse.error().data(e);
        }
    }

    // user groups
    public CEServiceResponse saveUserGroup(UserGroup group) {
        try {
            UserGroup savedGroup = userGroupRepository.save(group);
            log.info(String.format("User Group %s saved successfully", group.getName()));
            return CEServiceResponse.success().data(savedGroup);
        } catch (RuntimeException e) {
            log.error("Error saving user group: " + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse addToGroup(String userId, String groupId) {
        try {
            if (userRepository.findById(userId).isEmpty())
                throw new CEWebRequestError(
                        String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "user")
                );
            UserGroup userGroup = userGroupRepository.findById(groupId).orElseThrow();
            User user = userRepository.findById(userId).get();
            List<Role> userRoles = user.getRoles();
            if (userRoles.stream().noneMatch(r -> r.getAuthority() != null && r.getAuthority().equals(userGroup.getRole())))
                throw new CEWebRequestError(CEUserErrorMessage.GROUP_ROLE_NOT_MATCHED_WITH_USER);
            userGroup.addUsers(user);
            return CEServiceResponse.error().data(userGroup);
        } catch (RuntimeException e) {
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse loadGroupById(String id) {
        try {
            UserGroup group = userGroupRepository.findById(id).orElseThrow();
            return CEServiceResponse.success().data(group);
        } catch (RuntimeException e) {
            log.error("Error retrieving user group " + id);
            return CEServiceResponse.error().data(e);
        }
    }
}
