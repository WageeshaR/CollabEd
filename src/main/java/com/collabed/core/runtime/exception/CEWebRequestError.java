package com.collabed.core.runtime.exception;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public class CEWebRequestError extends RuntimeException {
    public CEWebRequestError(String error) {
        super(error);
    }
}
