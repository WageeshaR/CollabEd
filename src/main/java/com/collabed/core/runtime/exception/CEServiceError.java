package com.collabed.core.runtime.exception;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public class CEServiceError extends RuntimeException {
    public CEServiceError(String error) {
        super(error);
    }
}
