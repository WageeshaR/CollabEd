package com.collabed.core.api.controller.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("students")
@AllArgsConstructor
public class StudentController {
    private final UserService userService;
}
