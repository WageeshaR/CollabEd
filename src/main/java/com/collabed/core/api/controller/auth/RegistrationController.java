package com.collabed.core.api.controller.auth;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.data.model.user.User;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.UserService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
@AllArgsConstructor
@Log4j2
public class RegistrationController {
    private final UserService userService;
    private InstitutionService institutionService;
    private BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Endpoint to enter new students into the system
     * @param student user entity to be saved as a student
     * @param errors jakarta validation errors
     * @return ResponseEntity<?> object
     */
    @PostMapping("/student")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody User student, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    HTTPResponseErrorFormatter.format(errors)
            ));
        } else {
            student.setPassword(passwordEncoder.encode(student.getPassword()));

            CEServiceResponse savedStudent = userService.saveUser(student, "ROLE_STUDENT");
            if (savedStudent.isSuccess())
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                jwtTokenUtil.generateToken(student)
                        )
                        .body(savedStudent.getData());

            return ResponseEntity.internalServerError().body(savedStudent.getData());
        }
    }

    /**
     * Endpoint to enter new facilitators into the system
     * @param facilitator user entity to be saved as a facilitator
     * @param errors jakarta validation errors
     * @return ResponseEntity<?> object
     */
    @PostMapping("/facilitator")
    public ResponseEntity<?> registerFacilitator(@Valid @RequestBody User facilitator, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    HTTPResponseErrorFormatter.format(errors)
            ));
        } else {
            facilitator.setPassword(passwordEncoder.encode(facilitator.getPassword()));

            CEServiceResponse savedFacilitator = userService.saveUser(facilitator, "ROLE_FACILITATOR");
            if (savedFacilitator.isSuccess())
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                jwtTokenUtil.generateToken(facilitator)
                        )
                        .body(savedFacilitator.getData());

            return ResponseEntity.internalServerError().body(savedFacilitator.getData());
        }
    }

    /**
     * Endpoint to enter new admins into the system
     * @param admin user entity to be saved as a admin
     * @param errors jakarta validation errors
     * @return ResponseEntity<?> object
     */
    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody User admin, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    HTTPResponseErrorFormatter.format(errors)
            ));
        } else {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));

            CEServiceResponse savedAdmin = userService.saveUser(admin, "ROLE_ADMIN");
            if (savedAdmin.isSuccess())
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                jwtTokenUtil.generateToken(admin)
                        )
                        .body(savedAdmin.getData());

            return ResponseEntity.internalServerError().body(savedAdmin.getData());
        }
    }


    /**
     * Endpoint to enter new institutions into the system
     * @param institution Institution entity to be saved
     * @param errors jakarta validation errors
     * @return ResponseEntity<?> object
     */
    @PostMapping("/institution")
    public ResponseEntity<?> registerInstitution(@Valid @RequestBody Institution institution, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    HTTPResponseErrorFormatter.format(errors)
            ));
        } else {
            CEServiceResponse response = institutionService.save(institution);
            if (response.isSuccess())
                return ResponseEntity.status(HttpStatus.CREATED).body(response.getData());

            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    (Exception) response.getData()
            ));
        }
    }
}
