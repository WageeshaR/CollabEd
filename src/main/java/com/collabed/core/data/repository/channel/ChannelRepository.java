package com.collabed.core.data.repository.channel;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {
    @Query("{ deleted: false }")
    List<Channel> findAll();
    @Query("{ deleted: false, _id: ?0 }")
    Optional<Channel> findById(String id);
    @Query("{ deleted: false, name: ?0 }")
    Optional<Channel> findByName(String name);
    @Query("{ deleted: false, topic: ?0 }")
    List<Channel> findAllByTopic(Topic topic);
    @Query("{ _id: ?0 }")
    @Update("{$set: { 'deleted': true }}")
    void updateAndSoftDelete(String id);
}
