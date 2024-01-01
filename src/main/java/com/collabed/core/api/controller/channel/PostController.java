package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.proxy.PostProxy;
import com.collabed.core.runtime.exception.CEServiceError;
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

    @PostMapping
    public ResponseEntity<?> savePost(Authentication authentication, @Valid @RequestBody Post post, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));
        }
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
            List<Post> posts = postService.getAllPosts(username, channelId);
            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
