package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.proxy.PostProxy;
import com.collabed.core.data.repository.channel.PostRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.mongodb.MongoException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final int DEFAULT_FETCH_LIMIT = 10;

    public List<Post> getAllPosts(String username, String channelId) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Pageable pageable = PageRequest.of(0, DEFAULT_FETCH_LIMIT, Sort.Direction.DESC, "createdDate");
            return summarisePosts(
                postRepository.findAllByAuthorAndChannelId(user, channelId, pageable).getContent()
            );
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "user"));
        } catch (Exception e) {
            throw new CEServiceError(e.getMessage());
        }
    }

    public List<Post> getAllPosts(String channelId) {
        try {
            Pageable pageable = PageRequest.of(0, DEFAULT_FETCH_LIMIT, Sort.Direction.DESC, "createdDate");
            return summarisePosts(
                    postRepository.findAllByChannelId(channelId, pageable).getContent()
            );
        } catch (Exception e) {
            throw new CEServiceError(e.getMessage());
        }
    }

    public Post getPostById(String id) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Post post = postRepository.findById(id).orElseThrow();
            if (!Objects.equals(post.getAuthor().getUsername(), username))
                return new PostProxy(post);
            return post;
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(
                String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "post")
            );
        }
    }

    public List<Post> getAllChildrenSummary(String id) {
        try {
            Post parent = postRepository.findById(id).orElseThrow();
            Optional<List<Post>> optionalChildren = postRepository.findAllByParentEquals(parent);
            return optionalChildren.map(this::summarisePosts).orElseGet(() -> optionalChildren.orElseGet(List::of));
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(e.getMessage());
        }
    }

    public Post savePost(String username, Post post) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            post.setAuthor(user);
            return postRepository.save(post);
        } catch (DuplicateKeyException e) {
            throw new CEWebRequestError(
                String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "post") + ":\n" + e.getMessage()
            );
        } catch (Exception e) {
            throw new CEServiceError(
                String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "post")
            );
        }
    }

    private List<Post> summarisePosts(List<Post> posts) {
        List<Post> summarisedPosts = new ArrayList<>(posts);
        for (Post post : summarisedPosts) {
            post.setAuthor(null);
            if (!post.getTitle().isEmpty())
                post.setContent(null);
            post.getChannel().clearAudits();
        }
        return summarisedPosts;
    }
}
