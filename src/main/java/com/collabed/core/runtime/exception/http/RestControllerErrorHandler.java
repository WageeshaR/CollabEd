package com.collabed.core.runtime.exception.http;

import com.collabed.core.data.model.ApiError;
import com.collabed.core.runtime.exception.CEOperationNotAllowedError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestControllerErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CEOperationNotAllowedError.class)
    public ResponseEntity<?> handleOperationNotAllowed(CEOperationNotAllowedError ex) {
        ApiError error = new ApiError(HttpStatus.NOT_ACCEPTABLE);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        ApiError error = new ApiError(HttpStatus.UNAUTHORIZED);
        error.setMessage(ex.getClass().getName());
        return buildResponseEntity(error);
    }

    private ResponseEntity<?> buildResponseEntity(ApiError apiError) {
        return ResponseEntity.status(apiError.getStatus()).body(apiError.getMessage());
    }
}
