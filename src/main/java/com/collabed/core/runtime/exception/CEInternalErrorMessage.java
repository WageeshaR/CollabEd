package com.collabed.core.runtime.exception;

public class CEInternalErrorMessage {
    public static String MAPPING_FOR_GIVEN_NAME_UNAVAILABLE = "Provided object name is not available for mapping.";
    public static String SERVICE_QUERY_FAILED = "Error querying %s database"; // service name
    public static String SERVICE_UPDATE_FAILED = "Error updating %s database"; // service name
    public static String SERVICE_RUNTIME_ERROR = "Runtime error in %s service"; // service name
    public static String SERVICE_OPERATION_FAILED = "%s service failed to %s"; // service name, operation
    public static String GATEWAY_OPERATION_FAILED = "Failed to %s %s gateway"; // operation, gateway name
}
