package com.collabed.core.api;

import com.collabed.core.runtime.exception.OperationNotAllowedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestControllerErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OperationNotAllowedException.class)
    protected ResponseEntity<Object> handleOperationNotAllowed(OperationNotAllowedException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_ACCEPTABLE);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
