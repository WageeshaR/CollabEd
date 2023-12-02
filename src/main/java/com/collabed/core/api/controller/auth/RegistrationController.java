package com.collabed.core.api.controller.auth;

import com.collabed.core.data.model.User;
import com.collabed.core.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
@AllArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/student")
    public ResponseEntity<?> register(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    errors.getAllErrors().stream().map(
                            ObjectError::getDefaultMessage
                    )
            );
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerStudent(user));
        }
    }

    @PostMapping("/facilitator")
    public ResponseEntity<?> registerFacilitator(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    errors.getAllErrors().stream().map(
                            ObjectError::getDefaultMessage
                    )
            );
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerFacilitator(user));
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    errors.getAllErrors().stream().map(
                            ObjectError::getDefaultMessage
                    )
            );
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerAdmin(user));
        }
    }
}
