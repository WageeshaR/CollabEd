package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends MongoRepository<User, Long> {
    Optional<List<User>> findAllByRole(Role role);
    Optional<User> findByUsername(String username);
}
