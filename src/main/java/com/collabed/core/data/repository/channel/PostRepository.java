package com.collabed.core.data.repository.channel;

import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Optional<List<Post>> findAllByParentEquals(Post post);
    Page<Post> findAllByChannelId(String channelId, Pageable pageable);
    Page<Post> findAllByAuthorAndChannelId(User user, String channelId, Pageable pageable);
}
