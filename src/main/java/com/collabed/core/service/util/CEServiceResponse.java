package com.collabed.core.service.util;

import lombok.Data;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Data
public class CEServiceResponse {
    private boolean success;
    private boolean error;
    private String message;
    private Object data;
    public CEServiceResponse(boolean success, boolean error, String message, Object data) {
        this.success = success;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public CEServiceResponse(boolean success, boolean error, String message) {
        this.success = success;
        this.error = error;
        this.message = message;
    }

    public static Builder success() {
        return new ServiceResponseBuilder(true, false, null);
    }

    public static Builder success(String message) {
        return new ServiceResponseBuilder(true, false, message);
    }

    public static Builder error() {
        return new ServiceResponseBuilder(false, true, null);
    }

    public static Builder error(String message) {
        return new ServiceResponseBuilder(false, true, message);
    }

    public interface Builder {
        CEServiceResponse data(Object data);
        CEServiceResponse build();
    }

    public static class ServiceResponseBuilder implements Builder {
        private final boolean success;
        private final boolean error;
        private final String message;

        private ServiceResponseBuilder(boolean success, boolean error, String message) {
            this.success = success;
            this.error = error;
            this.message = message;
        }
        @Override
        public CEServiceResponse data(Object data) {
            return new CEServiceResponse(this.success, this.error, this.message, data);
        }

        @Override
        public CEServiceResponse build() {
            return new CEServiceResponse(this.success, this.error, this.message);
        }
    }
}
