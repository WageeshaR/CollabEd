package com.collabed.core.api.controller;

import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.service.InstitutionService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@RestController
@RequestMapping("institutions")
@AllArgsConstructor
public class InstitutionController {
    private final InstitutionService institutionService;
    @GetMapping
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<?> all() {
        var response = institutionService.getAll();

        return response.isSuccess() ?
                ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(response.getData());
    }
}
