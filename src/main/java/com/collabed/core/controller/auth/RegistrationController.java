package com.collabed.core.controller.auth;

import com.collabed.core.data.model.User;
import com.collabed.core.runtime.exception.OperationNotAllowedException;
import com.collabed.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
@AllArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
    }
}
