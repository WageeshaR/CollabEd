package com.collabed.core.api.controller.user;

import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.UserGroup;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    // users
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            User user = userService.findUser(id);
            return ResponseEntity.ok().body(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.fillInStackTrace()
            ));
        }
    }
    @GetMapping
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok().body(userService.getAll());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.fillInStackTrace()
            ));
        }
    }

    @GetMapping("/admins")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public ResponseEntity<?> getAllAdmins() {
        try {
            return ResponseEntity.ok().body(userService.getAll("ROLE_ADMIN"));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.fillInStackTrace()
            ));
        }
    }

    @GetMapping("/students")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN", "FACILITATOR"})
    public ResponseEntity<?> getAllStudents() {
        try {
            return ResponseEntity.ok().body(userService.getAll("ROLE_STUDENT"));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.fillInStackTrace()
            ));
        }
    }

    @GetMapping("/facilitators")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public ResponseEntity<?> getAllFacilitators() {
        try {
            return ResponseEntity.ok().body(userService.getAll("ROLE_FACILITATOR"));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.fillInStackTrace()
            ));
        }
    }

    @PatchMapping("/delete/{id}")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
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
