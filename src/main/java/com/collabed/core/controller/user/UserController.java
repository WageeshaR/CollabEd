package com.collabed.core.controller.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/admins")
    public List<User> getAllAdmins() {
        return userRepository.findAllByRole(Role.ADMIN).orElseGet(List::of);
    }

    @GetMapping("/students")
    public List<User> getAllStudents() {
        return userRepository.findAllByRole(Role.STUDENT).orElseGet(List::of);
    }

    @GetMapping("/facilitators")
    public List<User> getAllFacilitators() {
        return userRepository.findAllByRole(Role.FACILITATOR).orElseGet(List::of);
    }
}
