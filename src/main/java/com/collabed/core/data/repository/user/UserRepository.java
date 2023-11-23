package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.AbstractList;
import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends MongoRepository<User, Long> {
    Optional<List<User>> findAllByRole(Role role);
}
