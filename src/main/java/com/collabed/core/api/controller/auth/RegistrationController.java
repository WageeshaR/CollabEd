package com.collabed.core.api.controller.auth;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.dto.UserResponseDto;
import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.User;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
@AllArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private InstitutionService institutionService;
    private BCryptPasswordEncoder passwordEncoder;

    // users
    @PostMapping("/student")
    public ResponseEntity<?> register(@Valid @RequestBody User student, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        } else {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            try {
                UserResponseDto savedStudent = userService.saveUser(student, "STUDENT");
                return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
            } catch (DuplicateKeyException exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.toString());
            }
        }
    }

    @PostMapping("/facilitator")
    public ResponseEntity<?> registerFacilitator(@Valid @RequestBody User facilitator, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        } else {
            facilitator.setPassword(passwordEncoder.encode(facilitator.getPassword()));
            try {
                UserResponseDto savedFacilitator = userService.saveUser(facilitator, "FACILITATOR");
                return ResponseEntity.status(HttpStatus.CREATED).body(savedFacilitator);
            } catch (DuplicateKeyException exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.toString());
            }
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody User admin, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        } else {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            try {
                UserResponseDto savedAdmin = userService.saveUser(admin, "ADMIN");
                return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
            } catch (DuplicateKeyException exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.toString());
            }
        }
    }

    // institutions
    @PostMapping("/institution")
    public ResponseEntity<?> registerInstitution(@Valid @RequestBody Institution institution, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        } else {
            try {
                Institution savedInstitution = institutionService.save(institution);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedInstitution);
            } catch (DuplicateKeyException exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.toString());
            }
        }
    }
}
