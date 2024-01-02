package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.Reaction;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.proxy.PostProxy;
import com.collabed.core.data.repository.channel.PostRepository;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.util.CEServiceResponse;
import com.collabed.core.util.LoggingMessage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Log4j2
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private static final int DEFAULT_FETCH_LIMIT = 10;

    public CEServiceResponse getAllPosts(String username, String channelId) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Pageable pageable = PageRequest.of(0, DEFAULT_FETCH_LIMIT, Sort.Direction.DESC, "createdDate");
            List<Post> summarisedPosts = summarisePosts(
                    postRepository.findAllByAuthorAndChannelId(user, channelId, pageable).getContent()
            );
            return CEServiceResponse.success().data(summarisedPosts);
        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "user")).data(e);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse getAllPosts(String channelId) {
        try {
            Pageable pageable = PageRequest.of(0, DEFAULT_FETCH_LIMIT, Sort.Direction.DESC, "createdDate");
            List<Post> summarisedPosts = summarisePosts(
                    postRepository.findAllByChannelId(channelId, pageable).getContent()
            );
            return CEServiceResponse.success().data(summarisedPosts);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse getPostById(String id) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Post post = postRepository.findById(id).orElseThrow();
            if (!Objects.equals(post.getAuthor().getUsername(), username))
                return CEServiceResponse.success().data(new PostProxy(post));
            return CEServiceResponse.success().data(post);
        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse getAllChildrenSummary(String id) {
        try {
            Post parent = postRepository.findById(id).orElseThrow();
            Optional<List<Post>> optionalChildren = postRepository.findAllByParentEquals(parent);
            List<Post> posts = optionalChildren.map(this::summarisePosts).orElseGet(() -> optionalChildren.orElseGet(List::of));
            return CEServiceResponse.success().data(posts);
        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse savePost(String username, Post post) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            post.setAuthor(user);
            Post savedPost = postRepository.save(post);
            return CEServiceResponse.success().data(savedPost);
        } catch (DuplicateKeyException e) {
            log.error(LoggingMessage.Error.DUPLICATE_KEY);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "post")).data(e);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "post")).data(e);
        }
    }

    public CEServiceResponse saveReaction(String username, Reaction reaction) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Post post = postRepository.findById(reaction.getPost().getId()).orElseThrow();
            if (!post.getAuthor().getId().equals(user.getId()))
                throw new IllegalAccessException();
            reaction.setUser(user);
            Reaction saved = mongoTemplate.save(reaction, "reaction");
            return CEServiceResponse.success().data(saved);
        } catch (IllegalAccessException e) {
            log.error(LoggingMessage.Error.ILLEGAL_ACCESS + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_NOT_BELONG_TO_USER, "post")).data(e);
        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "post")).data(e);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "post")).data(e);
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
