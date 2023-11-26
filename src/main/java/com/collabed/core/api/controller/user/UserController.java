package com.collabed.core.api.controller.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/admins")
    public List<User> getAllAdmins() {
        return userService.getAll(Role.ADMIN);
    }

    @GetMapping("/students")
    public List<User> getAllStudents() {
        return userService.getAll(Role.STUDENT);
    }

    @GetMapping("/facilitators")
    public List<User> getAllFacilitators() {
        return userService.getAll(Role.FACILITATOR);
    }
}
