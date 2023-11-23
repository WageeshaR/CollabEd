package com.collabed.core.controller.auth;

import com.collabed.core.data.model.User;
import com.collabed.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("register")
@AllArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @PostMapping
    public User register(@RequestBody User user) {
        return userService.register(user);
    }
}
