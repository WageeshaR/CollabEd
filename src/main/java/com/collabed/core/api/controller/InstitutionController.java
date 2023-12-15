package com.collabed.core.api.controller;

import com.collabed.core.data.model.Institution;
import com.collabed.core.service.InstitutionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("institutions")
@AllArgsConstructor
public class InstitutionController {
    private InstitutionService institutionService;
    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok().body(institutionService.getAll());
    }
}
