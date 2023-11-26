package com.collabed.core.service;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserService userService = new UserService(userRepository);
    private User user;

    @BeforeEach
    public void setUp() {
        List<User> students = Arrays.asList(new User("student1", Role.STUDENT), new User("student2", Role.STUDENT));
        List<User> facilitators = Arrays.asList(new User("fac1", Role.FACILITATOR), new User("fac2", Role.FACILITATOR));
        List<User> admins = Arrays.asList(new User("adm1", Role.ADMIN), new User("adm2", Role.ADMIN));
        Mockito.when(userRepository.findAllByRole(Role.STUDENT)).thenReturn(
          Optional.of(students)
        );
        Mockito.when(userRepository.findAll()).thenReturn(
                Stream.of(students, facilitators, admins).flatMap(Collection::stream).collect(Collectors.toList())
        );
        user = new User("testUser", Role.STUDENT);
        Mockito.when(userRepository.insert(user)).thenReturn(user);
    }

    @Test
    public void userServiceGetAllByRoleTest() {
        List<User> students = userService.getAll(Role.STUDENT);
        assertNotNull(students);
        assertEquals(students.size(), 2);
        students.forEach(e -> assertEquals(e.getRole(), Role.STUDENT));
    }

    @Test
    public void userServiceGetAllTest() {
        List<User> allUsers = userService.getAll();
        assertNotNull(allUsers);
        assertEquals(allUsers.stream().filter(e -> e.getRole() == Role.STUDENT).count(), 2);
        assertEquals(allUsers.stream().filter(e -> e.getRole() == Role.FACILITATOR).count(), 2);
        assertEquals(allUsers.stream().filter(e -> e.getRole() == Role.ADMIN).count(), 2);
    }

    @Test
    public void userServiceRegisterTest() {
        User user = userService.register(this.user);
        assertNotNull(user);
        assertEquals(user.getUsername(), "testUser");
    }
}
