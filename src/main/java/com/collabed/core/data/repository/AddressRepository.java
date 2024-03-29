package com.collabed.core.data.repository;

import com.collabed.core.data.model.location.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Repository
public interface AddressRepository extends MongoRepository<Address, String> {}
