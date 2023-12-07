package com.collabed.core.service;

import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.UserGroupRepository;
import com.collabed.core.data.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserGroupRepository userGroupRepository = Mockito.mock(UserGroupRepository.class);
    UserService userService = new UserService(userRepository, userGroupRepository);
    private User user;

    @BeforeEach
    public void setUp() {
        List<User> students = Arrays.asList(new User("student1", "STUDENT"), new User("student2", "STUDENT"));
        List<User> facilitators = Arrays.asList(new User("fac1", "FACILITATOR"), new User("fac2", "FACILITATOR"));
        List<User> admins = Arrays.asList(new User("adm1", "ADMIN"), new User("adm2", "ADMIN"));
        Mockito.when(userRepository.findAllByRoles_Authority("STUDENT")).thenReturn(
          Optional.of(students)
        );
        Mockito.when(userRepository.findAll()).thenReturn(
                Stream.of(students, facilitators, admins).flatMap(Collection::stream).collect(Collectors.toList())
        );
        user = new User("testUser", "STUDENT");
        Mockito.when(userRepository.insert(user)).thenReturn(user);
    }

    @Test
    public void userServiceGetAllByRoleTest() {
        List<User> students = userService.getAll("STUDENT");
        assertNotNull(students);
        assertEquals(students.size(), 2);
        students.forEach(e -> assertTrue(e.getRoles().stream().anyMatch(role -> Objects.equals(role.getAuthority(), "STUDENT"))));
    }

    @Test
    public void userServiceGetAllTest() {
        List<User> allUsers = userService.getAll();
        assertNotNull(allUsers);
        assertEquals(allUsers.stream().filter(e -> Objects.equals(e.getRoles().get(0).getAuthority(), "STUDENT")).count(), 2);
        assertEquals(allUsers.stream().filter(e -> Objects.equals(e.getRoles().get(0).getAuthority(), "FACILITATOR")).count(), 2);
        assertEquals(allUsers.stream().filter(e -> Objects.equals(e.getRoles().get(0).getAuthority(), "ADMIN")).count(), 2);
    }

    @Test
    public void userServiceRegisterTest() {
        UserResponseDto user = userService.saveUser(this.user, "STUDENT");
        assertNotNull(user);
        assertEquals(user.getUsername(), "testUser");
    }
}
