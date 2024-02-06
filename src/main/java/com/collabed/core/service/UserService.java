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
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javatuples.Pair;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Service
@Log4j2
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final ProfileRepository profileRepository;
    private final MongoTemplate mongoTemplate;

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
            if (isNewRole.test(Pair.with(user, role)))
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

    public CEServiceResponse createUserProfile(Profile profile) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Profile savedProfile = mongoTemplate.save(profile);

            user.setProfile(savedProfile);
            mongoTemplate.save(user);

            log.info("Successfully saved user profile.");
            return CEServiceResponse.success().data(savedProfile);
        } catch (RuntimeException e) {
            log.error("Error saving user profile: " + e);
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
            Optional<UserGroup> optionalGroup = userGroupRepository.findById(groupId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
                log.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "user or userGroup"));
                throw new CEWebRequestError(
                        String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "user or userGroup")
                );
            }

            UserGroup userGroup = optionalGroup.get();
            User user = optionalUser.get();

            List<Role> userRoles = optionalUser.get().getRoles();
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

    /**
     * Returning a predicate to check if a given role does not exist with the given user
     */
    static final Predicate<Pair<User, String>> isNewRole = pair -> pair.getValue0().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .noneMatch(r -> Objects.equals(r, pair.getValue1()));
}
