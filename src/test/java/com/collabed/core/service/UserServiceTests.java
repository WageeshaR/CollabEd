package com.collabed.core.service;

import com.collabed.core.data.dto.UserGroupResponseDto;
import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.runtime.exception.CEErrorMessage;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile({"test"})
public class UserServiceTests {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserGroupRepository userGroupRepository = Mockito.mock(UserGroupRepository.class);
    UserService userService = new UserService(userRepository, userGroupRepository);
    private User user;
    private UserGroup userGroup;

    @BeforeEach
    public void setupUser() {
        List<User> students = Arrays.asList(new User("student1", "ROLE_STUDENT"), new User("student2", "ROLE_STUDENT"));
        List<User> facilitators = Arrays.asList(new User("fac1", "ROLE_FACILITATOR"), new User("fac2", "ROLE_FACILITATOR"));
        List<User> admins = Arrays.asList(new User("adm1", "ROLE_ADMIN"), new User("adm2", "ROLE_ADMIN"));

        Mockito.when(userRepository.findAllByRoles_Authority("ROLE_STUDENT")).thenReturn(
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

    @BeforeEach
    public void setupGroup() {
        userGroup = new UserGroup();
        userGroup.setName("testUserGroup");
        Mockito.when(userGroupRepository.insert(userGroup)).thenReturn(userGroup);
    }

    @Test
    public void loadUserByUsernameTest() {
        UserDetails userDetails = userService.loadUserByUsername("testUser");
        assertEquals(userDetails.getUsername(), "testUser");
    }

    @Test
    public void userServiceGetAllByRoleTest() {
        List<User> students = userService.getAll("ROLE_STUDENT");
        assertNotNull(students);
        assertEquals(students.size(), 2);
        students.forEach(e -> assertTrue(e.getAuthorities().stream().anyMatch(role -> Objects.equals(role.getAuthority(), "ROLE_STUDENT"))));
    }

    @Test
    public void userServiceGetAllTest() {
        List<User> allUsers = userService.getAll();
        assertNotNull(allUsers);
        assertEquals(allUsers.stream().filter(u -> Objects.equals(u.getAuthorities().stream().toList().get(0).getAuthority(), "ROLE_STUDENT")).count(), 2);
        assertEquals(allUsers.stream().filter(u -> Objects.equals(u.getAuthorities().stream().toList().get(0).getAuthority(), "ROLE_FACILITATOR")).count(), 2);
        assertEquals(allUsers.stream().filter(u -> Objects.equals(u.getAuthorities().stream().toList().get(0).getAuthority(), "ROLE_ADMIN")).count(), 2);
    }

    @Test
    public void userServiceSaveUserTest() {
        UserResponseDto userDto = userService.saveUser(this.user, "ROLE_STUDENT");
        assertNotNull(userDto);
        assertEquals(userDto.getUsername(), "testUser");
    }

    @Test
    public void userServiceSaveUserWithExistingRoleTest() {
        user.addRole("ROLE_STUDENT");
        CEWebRequestError error = assertThrows(CEWebRequestError.class, () -> userService.saveUser(this.user, "ROLE_STUDENT"));
        assertEquals(error.getMessage(), CEErrorMessage.ROLE_ALREADY_EXISTS);
    }

    @Test
    public void userServiceSaveUserGroupTest() {
        UserGroup userGroup = userService.saveUserGroup(this.userGroup);
        assertNotNull(userGroup);
        assertEquals(userGroup.getName(), "testUserGroup");
    }

    @Test
    public void userServiceAddToGroupTest() {
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();
        user.setId(userId);
        user.addRole("ROLE_STUDENT");
        userGroup.setId(groupId);
        userGroup.setRole("ROLE_STUDENT");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(userGroup));
        UserGroup group = userService.addToGroup(userId, groupId);
        assertNotNull(group);
        assertEquals(group.getName(), "testUserGroup");
    }

    @Test
    public void userServiceAddToGroupNoUserTest() {
        CEWebRequestError error = assertThrows(CEWebRequestError.class, () -> userService.addToGroup(new ObjectId().toHexString(), new ObjectId().toHexString()));
        assertEquals(error.getMessage(), CEErrorMessage.USER_NOT_EXIST);
    }

    @Test
    public void userServiceAddToGroupRoleNotMatchedTest() {
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();
        user.setId(userId);
        user.addRole("ROLE_STUDENT");
        userGroup.setId(groupId);
        userGroup.setRole("ROLE_FACILITATOR");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(userGroup));
        CEWebRequestError error = assertThrows(CEWebRequestError.class, () -> userService.addToGroup(userId, groupId));
        assertEquals(error.getMessage(), CEErrorMessage.GROUP_ROLE_NOT_MATCHED_WITH_USER);
    }

    @Test
    public void userServiceLoadGroupByIdTest() {
        String userId = new ObjectId().toHexString();
        String groupId = new ObjectId().toHexString();
        user.setId(userId);
        userGroup.setId(groupId);
        userGroup.setUserIds(List.of(userId));
        Mockito.when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(userGroup));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserGroupResponseDto dto = userService.loadGroupById(groupId);
        assertNotNull(dto);
        assertEquals(dto.name(), "testUserGroup");
    }
}
