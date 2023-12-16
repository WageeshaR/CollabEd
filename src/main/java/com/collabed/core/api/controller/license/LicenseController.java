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
//    private final LicenseService licenseService;
    @GetMapping("/init")
    public ResponseEntity<?> initSession() {
        return ResponseEntity.ok().header(CustomHttpHeaders.SESSION_KEY, generateSessionKey(null)).build();
    }

//    @GetMapping("/options")
//    public ResponseEntity<?> getOptions() {
//        List<LicenseOption> licenseOptions = licenseService.getOptions();
//        return ResponseEntity.ok();
//    }
}
