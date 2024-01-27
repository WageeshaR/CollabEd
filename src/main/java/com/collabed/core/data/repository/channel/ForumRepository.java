package com.collabed.core.data.repository.channel;

import com.collabed.core.data.model.channel.Forum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {}
