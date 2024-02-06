package com.collabed.core.service.intel.criteria;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public enum CriteriaScope {
    FILTER, // produce a filtered collection based on a supplied criteria
    GENERATE, // generate a new outcome based on a supplied criteria
    QUERY, // answer a query on a supplied criteria
    SIMILARITY, // perform similarity evaluation between items for a given criteria
    PROXIMITY, // perform cluster-based proximity evaluation for a given criteria
}
