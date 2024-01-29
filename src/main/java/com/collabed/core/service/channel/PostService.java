package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.Reaction;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.proxy.PostProxy;
import com.collabed.core.data.repository.channel.PostRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import com.collabed.core.util.LoggingMessage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.Utils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

@Service
@AllArgsConstructor
@Log4j2
public class PostService {
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private static final int DEFAULT_FETCH_LIMIT = 10;

    public CEServiceResponse getAllPosts(String channelId, boolean personnel) {
        List<Post> summarisedPosts = null;

        try {
            Pageable pageable = PageRequest.of(0, DEFAULT_FETCH_LIMIT, Sort.Direction.DESC, "createdDate");

            if (personnel) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                summarisedPosts = summarisePosts(
                        postRepository.findAllByAuthorAndChannelId(user, channelId, pageable).getContent()
                );
            }
            else {
                summarisedPosts = summarisePosts(
                        postRepository.findAllByChannelId(channelId, pageable).getContent()
                );
            }

            return CEServiceResponse.success().data(summarisedPosts);
        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.NO_MATCHING_ELEMENTS_FOUND, "posts")).data(e);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse getPostById(String id) {
        try {
            Post post = postRepository.findById(id).orElseThrow();

            String currentUser = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            String author = post.getAuthor().getUsername();

            if (!Objects.equals(author, currentUser))
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

            List<Post> summarisedChildPosts = optionalChildren.map(this::summarisePosts).orElseThrow();
            return CEServiceResponse.success().data(summarisedChildPosts);
        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse savePost(Post post) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (post.getAuthor() != null && !post.getAuthor().getId().equals(user.getId()))
                throw new IllegalAccessException();

            else post.setAuthor(user);

            Post savedPost = postRepository.save(post);

            return CEServiceResponse.success().data(savedPost);
        } catch (IllegalAccessException e) {
            log.error(LoggingMessage.Error.ILLEGAL_MODIFICATION + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_NOT_BELONG_TO_USER, "post")).data(e);
        } catch (DuplicateKeyException e) {
            log.error(LoggingMessage.Error.DUPLICATE_KEY);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "post")).data(e);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "post")).data(e);
        }
    }

    public CEServiceResponse saveReaction(Reaction reaction) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Post post = postRepository.findById(reaction.getPost().getId()).orElseThrow();

            if (!post.getAuthor().getId().equals(user.getId()))
                throw new IllegalAccessException();

            reaction.setUser(user);

            Reaction saved = mongoTemplate.save(reaction, "reaction");
            return CEServiceResponse.success().data(saved);
        } catch (IllegalAccessException e) {
            log.error(LoggingMessage.Error.ILLEGAL_MODIFICATION + e);
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

            if (post.getTitle() != null && !post.getTitle().isEmpty())
                post.setContent(null);

            post.getChannel().clearAudits();
        }
        return summarisedPosts;
    }

    public Method returnSummarisedPostsWithAccess() throws NoSuchMethodException {
        Method summarisedPosts = this.getClass().getDeclaredMethod("summarisePosts", List.class);
        summarisedPosts.setAccessible(true);
        return summarisedPosts;
    }
}
