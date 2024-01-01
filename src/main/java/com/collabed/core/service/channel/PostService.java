package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.repository.channel.PostRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.mongodb.MongoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        try {
            return postRepository.findAll();
        } catch (MongoException e) {
            throw new CEServiceError(
                    String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "post")
            );
        }
    }

    public Post getPostById(String id) {
        try {
            return postRepository.findById(id).orElseThrow();
        } catch (MongoException e) {
            throw new CEWebRequestError(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
            );
        }
    }
}
