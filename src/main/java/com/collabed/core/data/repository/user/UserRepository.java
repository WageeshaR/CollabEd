package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.User;
import com.collabed.core.data.model.location.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends MongoRepository<User, String> {
    Optional<List<User>> findAllByRoles_Authority(String authority);
    Optional<User> findByUsername(String username);
}
