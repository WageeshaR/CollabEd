package com.collabed.core.data.repository;

import com.collabed.core.data.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, String> {}
