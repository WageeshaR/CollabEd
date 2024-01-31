package com.collabed.core.data.repository;

import com.collabed.core.data.model.license.LicenseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Repository
public interface LicenseRepository extends MongoRepository<LicenseModel, String> {
}
