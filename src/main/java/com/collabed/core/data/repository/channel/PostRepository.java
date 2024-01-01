package com.collabed.core.data.repository.channel;

import com.collabed.core.data.model.channel.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {}
