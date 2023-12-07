package com.collabed.core.data.repository;

import com.collabed.core.data.model.Institution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends MongoRepository<Institution, String> {}
