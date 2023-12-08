package com.collabed.core.runtime.exception;

public class CEOperationNotAllowedError extends RuntimeException {
    public CEOperationNotAllowedError(String error) {
        super(error);
    }
}
