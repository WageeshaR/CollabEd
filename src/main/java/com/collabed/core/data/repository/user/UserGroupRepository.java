package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.user.UserGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends MongoRepository<UserGroup, String> {}
