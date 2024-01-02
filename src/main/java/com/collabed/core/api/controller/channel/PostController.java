package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.Reaction;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.channel.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;
    private final ChannelRepository channelRepository;

    @PostMapping
    public ResponseEntity<?> savePost(Authentication authentication, @Valid @RequestBody Post post, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        if (post.getChannel().getId() == null) {
            return ResponseEntity.badRequest().body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    String.format(CEUserErrorMessage.ENTITY_PROPERTY_MUST_NOT_BE_NULL, "channel", "id")
            ));
        }
        if (channelRepository.findById(post.getChannel().getId()).isEmpty())
            return ResponseEntity.badRequest().body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
            ));
        try {
            String username = (String) authentication.getPrincipal();
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(username, post));
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (CEWebRequestError e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getPostById(@RequestParam(name = "id") String postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseEntity.ok().body(post);
        } catch (CEWebRequestError e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getAllPosts(Authentication authentication,
                                         @RequestParam(name = "channel") String channelId,
                                         @RequestParam(name = "personnel", required = false) boolean personnel) {
        String username = (String) authentication.getPrincipal();
        try {
            List<Post> posts = personnel ?
                    postService.getAllPosts(username, channelId) : postService.getAllPosts(channelId);
            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/reaction")
    public ResponseEntity<?> updateReaction(Authentication authentication,
                                            @Valid @RequestBody Reaction reaction, Errors errors) {
        String username = (String) authentication.getPrincipal();
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.saveReaction(username, reaction));
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
