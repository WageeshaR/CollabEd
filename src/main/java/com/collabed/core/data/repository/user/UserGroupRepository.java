package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.User;
import com.collabed.core.data.model.UserGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends MongoRepository<UserGroup, String> {
    Optional<List<UserGroup>> findAllByUserIdsContains(List<String> userIds);
}
