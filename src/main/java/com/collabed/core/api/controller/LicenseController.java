package com.collabed.core.api.controller;

import com.collabed.core.api.util.CustomHttpHeaders;
import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.Session;
import com.collabed.core.data.model.license.LicenseOption;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.service.license.LicenseService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.collabed.core.util.SessionUtil.generateSessionKey;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@RestController
@RequestMapping("license")
@AllArgsConstructor
public class LicenseController {
    private final LicenseService licenseService;
    @GetMapping("/get-key")
    public ResponseEntity<?> getKey() {
        return ResponseEntity.ok().header(
                CustomHttpHeaders.SESSION_KEY,
                generateSessionKey(null)
        ).build();
    }

    @GetMapping("/options")
    public ResponseEntity<?> getOptions() {
        var response = licenseService.getAllOptions();

        return response.isSuccess() ? ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getMessage(),
                        (Exception) response.getData()
                )
        );
    }

    @PostMapping("/select-option")
    public ResponseEntity<?> selectOption(@Valid @RequestBody LicenseOption option, Errors errors,
                                          @RequestHeader(CustomHttpHeaders.SESSION_KEY) String sessionKey) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        }

        var response = licenseService.initSession(option, sessionKey);

        return response.isSuccess() ? ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getMessage(),
                        (Exception) response.getData()
                )
        );
    }

    // TODO: add controller endpoint to update unitCount for user license object (validation: LicenseType.GROUP only)
}
