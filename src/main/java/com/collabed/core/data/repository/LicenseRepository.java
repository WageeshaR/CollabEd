package com.collabed.core.data.repository;

import com.collabed.core.data.model.license.LicenseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends MongoRepository<LicenseModel, String> {
}