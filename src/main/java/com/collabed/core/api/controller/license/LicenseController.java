package com.collabed.core.api.controller.license;

import com.collabed.core.api.util.CustomHttpHeaders;
import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.license.LicenseOption;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.service.license.LicenseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.collabed.core.api.util.LicenseUtil.generateSessionKey;

@RestController
@RequestMapping("license")
@AllArgsConstructor
public class LicenseController {
    private final LicenseService licenseService;
    @GetMapping("/get-key")
    public ResponseEntity<?> getKey() {
        return ResponseEntity.ok().header(CustomHttpHeaders.SESSION_KEY, generateSessionKey(null)).build();
    }

    @GetMapping("/options")
    public ResponseEntity<?> getOptions() {
        List<LicenseOption> licenseOptions = licenseService.getAllOptions();
        return ResponseEntity.ok().body(licenseOptions);
    }

    @PostMapping("/select-option")
    public ResponseEntity<?> selectOption(@Valid @RequestBody LicenseOption option, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        }
        try {
            boolean initLicenseBuilder = licenseService.initSession(option);
            if (initLicenseBuilder)
                return ResponseEntity.ok().build();
            else return ResponseEntity.internalServerError().build();
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e);
        }

    }
}
