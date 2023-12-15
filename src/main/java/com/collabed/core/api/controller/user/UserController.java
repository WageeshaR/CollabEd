package com.collabed.core.api.controller.user;

import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import com.collabed.core.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    // users
    @GetMapping
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/admins")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public List<User> getAllAdmins() {
        return userService.getAll("ROLE_ADMIN");
    }

    @GetMapping("/students")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN", "FACILITATOR"})
    public List<User> getAllStudents() {
        return userService.getAll("ROLE_STUDENT");
    }

    @GetMapping("/facilitators")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public List<User> getAllFacilitators() {
        return userService.getAll("ROLE_FACILITATOR");
    }

    // user groups
    @PostMapping("/groups")
    public ResponseEntity<?> createUserGroup(@Valid @RequestBody UserGroup group, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    errors.getAllErrors().stream().map(
                            ObjectError::getDefaultMessage
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUserGroup(group));
        }
    }

    @PostMapping("/groups/add-user")
    public ResponseEntity<?> addUserToGroup(@RequestBody Map<String, String> request) {
        if (request.get("user_id") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_id must be specified");
        }
        if (request.get("group_id") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("group_id must be specified");
        }
        try {
            UserGroup group = userService.addToGroup(request.get("user_id"), request.get("group_id"));
            return ResponseEntity.ok().body(group);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<?> groupDetails(@PathVariable String id) {
        try {
            return ResponseEntity.ok().body(userService.loadGroupById(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
