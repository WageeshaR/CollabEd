package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.AbstractList;
import java.util.List;
import java.util.Optional;

public interface AdminRepository extends MongoRepository<User, Long> {
    @NonNull
    default List<User> findAll() {
        Optional<User> result = findAllByRole(Role.ADMIN);
        if (result.isEmpty()) return new AbstractList<>() {
            @Override
            public User get(int index) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        };
        else return result.stream().toList();
    }

    Optional<User> findAllByRole(Role role);
    Optional<User> findByRole(Role role);
}
