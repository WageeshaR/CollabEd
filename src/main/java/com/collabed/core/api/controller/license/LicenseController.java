package com.collabed.core.api.controller.license;

import com.collabed.core.api.util.CustomHttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.collabed.core.api.util.LicenseUtil.generateSessionKey;

@RestController
@RequestMapping("/license")
public class LicenseController {
    @GetMapping("/init")
    public ResponseEntity<?> initSession() {
        return ResponseEntity.ok().header(CustomHttpHeaders.SESSION_KEY, generateSessionKey()).build();
    }
}
