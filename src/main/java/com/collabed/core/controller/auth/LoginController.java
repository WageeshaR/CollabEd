package com.collabed.core.controller.auth;

import com.collabed.core.data.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginController {
    @PostMapping
    public ResponseEntity<Object> login(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }
}
