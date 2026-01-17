package com.collabed.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.UserGroup;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.data.repository.user.ProfileRepository;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.util.CEServiceResponse;
import com.collabed.core.service.util.SecurityUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetails;

@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @InjectMocks
    private UserService userService;
    private User user;
    private UserGroup userGroup;

    private Predicate<User> roleFilter(String role) {
        return u -> Objects.equals(
                u.getAuthorities().stream().toList().get(0).getAuthority(), role
        );
    }

//    @BeforeEach
    void setupUser() {
        List<User> students = Arrays.asList(new User("student1", "ROLE_STUDENT"), new User("student2", "ROLE_STUDENT"));
        List<User> facilitators = Arrays.asList(new User("fac1", "ROLE_FACILITATOR"), new User("fac2", "ROLE_FACILITATOR"));
        List<User> admins = Arrays.asList(new User("adm1", "ROLE_ADMIN"), new User("adm2", "ROLE_ADMIN"));

        Mockito.when(userRepository.findAllByAuthority("ROLE_STUDENT")).thenReturn(
                Optional.of(students)
        );
        Mockito.when(userRepository.findAll()).thenReturn(
                Stream.of(students, facilitators, admins).flatMap(Collection::stream).collect(Collectors.toList())
        );

        Institution mockInstitution = Mockito.mock(Institution.class);
        user = new User("testUser", null);
        user.setInstitution(mockInstitution);

        Mockito.when(userRepository.insert(user)).thenReturn(user);
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.ofNullable(user));
    }

//    @BeforeEach
    void setupGroup() {
        userGroup = new UserGroup();
        userGroup.setName("testUserGroup");
        Mockito.when(userGroupRepository.insert(userGroup)).thenReturn(userGroup);
    }

    @Test
    void loadUserByUsernameTest() {
        Institution mockInstitution = Mockito.mock(Institution.class);

        user = new User("testUser", null);
        user.setInstitution(mockInstitution);

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.ofNullable(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");
        assertEquals(userDetails.getUsername(), "testUser");
    }

    @Test
    void userServiceGetAllByRoleTest() {
        List<User> students = Arrays.asList(new User("student1", "ROLE_STUDENT"), new User("student2", "ROLE_STUDENT"));

        Mockito.when(userRepository.findAllByAuthority("ROLE_STUDENT")).thenReturn(
                Optional.of(students)
        );

        List<User> allStudents = userService.getAll("ROLE_STUDENT");
        assertNotNull(allStudents);
        assertEquals(2, allStudents.size());
        allStudents.forEach(e -> assertTrue(e.getAuthorities().stream().anyMatch(role -> Objects.equals(role.getAuthority(), "ROLE_STUDENT"))));
    }

    @Test
    void userServiceGetAllTest() {
        List<User> students = Arrays.asList(new User("student1", "ROLE_STUDENT"), new User("student2", "ROLE_STUDENT"));
        List<User> facilitators = Arrays.asList(new User("fac1", "ROLE_FACILITATOR"), new User("fac2", "ROLE_FACILITATOR"));
        List<User> admins = Arrays.asList(new User("adm1", "ROLE_ADMIN"), new User("adm2", "ROLE_ADMIN"));

        Mockito.when(userRepository.findAll()).thenReturn(
                Stream.of(students, facilitators, admins).flatMap(Collection::stream).collect(Collectors.toList())
        );

        List<User> allUsers = userService.getAll();

        assertNotNull(allUsers);
        assertEquals(allUsers.stream().filter(roleFilter("ROLE_STUDENT")).count(), 2);
        assertEquals(allUsers.stream().filter(roleFilter("ROLE_FACILITATOR")).count(), 2);
        assertEquals(allUsers.stream().filter(roleFilter("ROLE_ADMIN")).count(), 2);
    }

    @Test
    void userServiceSaveUserTest() {
        Institution mockInstitution = Mockito.mock(Institution.class);

        user = new User("testUser", null);
        user.setInstitution(mockInstitution);

        Mockito.when(userRepository.insert(Mockito.any(User.class))).thenReturn(user);

        CEServiceResponse response = userService.saveUser(this.user, "ROLE_STUDENT");

        assertNotNull(response.getData());
        assertEquals(((User) response.getData()).getUsername(), "testUser");
    }

    @Test
    void userServiceSaveUserWithExistingRoleTest() {
        Institution mockInstitution = Mockito.mock(Institution.class);

        user = new User("testUser", null);
        user.setInstitution(mockInstitution);
        user.addRole("ROLE_STUDENT");

        CEServiceResponse response = userService.saveUser(this.user, "ROLE_STUDENT");

        assertTrue(response.isError());
        assertEquals(
                response.getData(),
                String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "role")
        );
    }

    @Test
    void deleteUserTest() {
        Mockito.doNothing().when(userRepository).updateAndSoftDelete(Mockito.anyString());
        userService.deleteUser(new ObjectId().toHexString());
    }

    @Test
    void deleteUserErrorTest() {
        Mockito.doThrow(new CEServiceError("")).when(userRepository).updateAndSoftDelete(Mockito.anyString());

        CEServiceResponse response = userService.deleteUser(new ObjectId().toHexString());

        assertTrue(response.isError());
    }

    @Test
    void createUserProfileTest() {
        Institution mockInstitution = Mockito.mock(Institution.class);

        user = new User("testUser", null);
        user.setInstitution(mockInstitution);

        Mockito.when(SecurityUtil.withAuthentication().getPrincipal()).thenReturn(user);

        Profile profile = Mockito.mock(Profile.class);

        Mockito.when(mongoTemplate.save(profile)).thenReturn(profile);
        Mockito.when(mongoTemplate.save(user)).thenReturn(user);

        CEServiceResponse response = userService.createUserProfile(profile);
        assertTrue(response.isSuccess());
        assertInstanceOf(Profile.class, response.getData());
    }

    @Test
    void createUserProfileErrorTest() {
        Institution mockInstitution = Mockito.mock(Institution.class);
        user = new User("testUser", null);
        user.setInstitution(mockInstitution);

        Mockito.when(SecurityUtil.withAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(mongoTemplate.save(Mockito.any(Profile.class))).thenThrow(RuntimeException.class);

        CEServiceResponse response1 = userService.createUserProfile(Mockito.mock(Profile.class));
        assertTrue(response1.isError());

        Mockito.when(mongoTemplate.save(Mockito.any(Profile.class))).thenReturn(Mockito.mock(Profile.class));
        Mockito.when(mongoTemplate.save(user)).thenThrow(RuntimeException.class);

        CEServiceResponse response2 = userService.createUserProfile(Mockito.mock(Profile.class));
        assertTrue(response2.isError());
    }

    @Test
    void userServiceSaveUserGroupTest() {
        userGroup = new UserGroup();
        userGroup.setName("testUserGroup");

        Mockito.when(userGroupRepository.save(Mockito.any(UserGroup.class))).thenReturn(this.userGroup);

        CEServiceResponse userGroup = userService.saveUserGroup(this.userGroup);

        assertNotNull(userGroup.getData());
        assertEquals(((UserGroup) userGroup.getData()).getName(), "testUserGroup");
    }

    @Test
    void userServiceAddToGroupTest() {
        Institution mockInstitution = Mockito.mock(Institution.class);
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();

        user = new User("testUser", null);
        user.setInstitution(mockInstitution);
        user.setId(userId);
        user.addRole("ROLE_STUDENT");

        userGroup = new UserGroup();
        userGroup.setName("testUserGroup");
        userGroup.setId(groupId);
        userGroup.setRole("ROLE_STUDENT");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(userGroup));

        CEServiceResponse group = userService.addToGroup(userId, groupId);

        assertNotNull(group);
        assertEquals(((UserGroup) group.getData()).getName(), "testUserGroup");
    }

    @Test
    void userServiceAddToGroupNoUserTest() {
        CEServiceResponse response = userService.addToGroup(new ObjectId().toHexString(), new ObjectId().toHexString());
        assertTrue(response.isError());
        assertEquals(
                ((CEWebRequestError) response.getData()).getMessage(),
                String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "user or userGroup")
        );
    }

    @Test
    void userServiceAddToGroupRoleNotMatchedTest() {
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();

        user = new User("testUser", null);
        user.setId(userId);
        user.addRole("ROLE_STUDENT");

        userGroup = new UserGroup();
        userGroup.setName("testUserGroup");
        userGroup.setId(groupId);
        userGroup.setRole("ROLE_FACILITATOR");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(userGroup));

        CEServiceResponse response = userService.addToGroup(userId, groupId);
        assertTrue(response.isError());
        assertEquals(
                ((CEWebRequestError) response.getData()).getMessage(),
                CEUserErrorMessage.GROUP_ROLE_NOT_MATCHED_WITH_USER);
    }

    @Test
    void userServiceLoadGroupByIdTest() {
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();

        user = new User("testUser", null);
        user.setId(userId);

        userGroup = new UserGroup();
        userGroup.setId(groupId);
        userGroup.setName("testUserGroup");
        userGroup.setUsers(List.of(user));

        Mockito.when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(userGroup));

        CEServiceResponse group = userService.loadGroupById(groupId);

        assertNotNull(group);
        assertEquals(((UserGroup) group.getData()).getName(), "testUserGroup");
    }
}
