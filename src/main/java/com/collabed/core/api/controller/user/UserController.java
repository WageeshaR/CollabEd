package com.collabed.core.api.controller.user;

import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.UserGroup;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.service.UserService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

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
        CEServiceResponse user = userService.findUser(id);
        return user.isSuccess() ?
                ResponseEntity.ok().body(user.getData()) : ResponseEntity.internalServerError().body(user.getData());
    }
    @GetMapping
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok().body(userService.getAll());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
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
                    e
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
                    e
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
                    e
            ));
        }
    }

    @PatchMapping("/delete/{id}")
    @RolesAllowed({"SUPER_ADMIN", "ADMIN"})
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        CEServiceResponse deleted = userService.deleteUser(id);
        return deleted.isSuccess() ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                deleted.getData().toString()
        ));
    }

    @PostMapping("profile")
    public ResponseEntity<?> createProfile(@Valid @RequestBody Profile profile, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    errors.getAllErrors().stream().map(ObjectError::getDefaultMessage)
            );
        }
        CEServiceResponse response = userService.createUserProfile(profile);
        return response.isSuccess() ?
                ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    // user groups
    @PostMapping("/groups")
    public ResponseEntity<?> createUserGroup(@Valid @RequestBody UserGroup group, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    errors.getAllErrors().stream().map(ObjectError::getDefaultMessage)
            );
        }
        CEServiceResponse saved = userService.saveUserGroup(group);
        if (saved.isSuccess())
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getData());
        return ResponseEntity.internalServerError().body(saved.getData());
    }

    @PostMapping("/groups/add-user")
    public ResponseEntity<?> addUserToGroup(@RequestBody Map<String, String> request) {
        if (request.get("user_id") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_id must be specified");
        }
        if (request.get("group_id") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("group_id must be specified");
        }
        CEServiceResponse group = userService.addToGroup(request.get("user_id"), request.get("group_id"));
        return group.isSuccess() ?
                ResponseEntity.ok().body(group.getData()) : ResponseEntity.internalServerError().body(group.getData());
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<?> groupDetails(@PathVariable String id) {
        CEServiceResponse group = userService.loadGroupById(id);
        return group.isSuccess() ?
                ResponseEntity.ok().body(group.getData()) : ResponseEntity.internalServerError().body(group.getData());
    }
}
