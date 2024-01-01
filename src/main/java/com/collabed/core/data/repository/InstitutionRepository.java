package com.collabed.core.data.repository;

import com.collabed.core.data.model.institution.Institution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends MongoRepository<Institution, String> {}
