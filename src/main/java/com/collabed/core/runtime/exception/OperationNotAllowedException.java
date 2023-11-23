package com.collabed.core.runtime.exception;

public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String error) {
        super(error);
    }
}
