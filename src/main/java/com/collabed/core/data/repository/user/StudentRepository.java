package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.AbstractList;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<User, Long> {
    @NonNull
    default List<User> findAll() {
        Optional<List<User>> result = findAllByRole(Role.STUDENT);
        return result.orElseGet(() -> new AbstractList<>() {
            @Override
            public User get(int index) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        });
    }

    Optional<List<User>> findAllByRole(Role role);
}
