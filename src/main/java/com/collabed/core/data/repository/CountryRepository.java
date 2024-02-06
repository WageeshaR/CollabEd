package com.collabed.core.data.repository;

import com.collabed.core.data.model.location.Country;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public interface CountryRepository extends MongoRepository<Country, String> {}
