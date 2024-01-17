package com.collabed.core.data.repository.channel;

import com.collabed.core.api.controller.channel.VisibilityEnum;
import com.collabed.core.data.model.channel.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {
    @Query("{ deleted: false }")
    @Nonnull
    List<Channel> findAll();

    @Query("{ deleted: false, _id: ?0 }")
    @Nonnull
    Optional<Channel> findById(@Nonnull String id);

    @Query("{ deleted: false, name: ?0 }")
    Optional<Channel> findByName(String name);

    @Query("{ deleted: false, 'topic.name': ?0 }")
    List<Channel> findAllByTopic(String topicName);

    @Query("{ _id: ?0, createdByUser: ?1 }")
    @Update("{$set: { 'deleted': true }}")
    void updateAndSoftDelete(String id, String createdBy);

    @Query("{ _id: ?0, createdByUser: ?1 }")
    @Update("{$set: { 'visibility': ?2 }}")
    int updateVisibility(String id, String createdBy, VisibilityEnum visibility);
}
