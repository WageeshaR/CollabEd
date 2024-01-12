package com.collabed.core.internal;

import java.util.List;

/**
 * Base CollabEd gateway interface for all internal gateways e.g., payments, intel, etc.
 */
public interface CEGateway {
    /**
     * Initialise the gateway into required state
     * @return boolean indicating success or failure in initialisation attempt
     */
    boolean initialise();

    /**
     * Perform authentication against the target client
     */
    void authenticate();

    /**
     * A boolean check on whether the gateway holds result of the operation
     * @return boolean indicating availability of results
     */
    boolean hasResult();

    /**
     * return the result in a List<T> from the gateway operation
     * @return result of type List<T>
     */
    List<?> returnListResult();

    /**
     * return the result from the gateway operation
     * @return result of type T
     */
    Object returnResult();
}
