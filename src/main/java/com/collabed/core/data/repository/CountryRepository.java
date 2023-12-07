package com.collabed.core.data.repository;

import com.collabed.core.data.model.location.Country;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CountryRepository extends MongoRepository<Country, String> {}
