package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Repository
public interface UserRepository
        extends MongoRepository<User, String> {
    @Query("{ deleted: false, 'roles.authority': ?0 }")
    Optional<List<User>> findAllByAuthority(String authority);
    @Query("{ deleted: false, username: ?0 }")
    Optional<User> findByUsername(String username);
    @Query("{ deleted: false }")
    List<User> findAll();
    @Query("{ deleted: false, _id: ?0 }")
    Optional<User> findById(String id);
    @Query("{ _id: ?0 }")
    @Update("{$set: { 'deleted': true }}")
    void updateAndSoftDelete(String id);
}
