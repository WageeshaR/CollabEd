package com.collabed.core.data.repository;

import com.collabed.core.data.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public interface SessionRepository extends MongoRepository<Session, String> {}
