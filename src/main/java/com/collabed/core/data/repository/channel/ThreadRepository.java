package com.collabed.core.data.repository.channel;

import com.collabed.core.data.model.channel.Thread;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Repository
public interface ThreadRepository extends MongoRepository<Thread, String> {}
