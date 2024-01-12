package com.collabed.core.util;

public class LoggingMessage {
    public static class Success {
        public static final String SAVE = "Entity saved successfully.";
        public static final String UPDATE = "Entity updated successfully.";
        public static final String DELETE = "Entity deleted successfully.";
    }

    public static class Error {
        public static final String SERVICE = "Service error: ";
        public static final String DUPLICATE_KEY = "Duplicate key error: ";
        public static final String NO_SUCH_ELEMENT = "No such element error: ";
        public static final String ILLEGAL_MODIFICATION = "Illegal modification attempt on %s by user %s";
    }
}
