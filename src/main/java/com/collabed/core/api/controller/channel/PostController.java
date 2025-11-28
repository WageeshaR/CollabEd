package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.Reaction;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.channel.PostService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
@Log4j2
public class PostController {
    private final PostService postService;
    private final ChannelRepository channelRepository;

    @PostMapping
    public ResponseEntity<?> savePost(@Valid @RequestBody Post post, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));

        if (post.getChannel() == null || post.getChannel().getId() == null) {
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

        var response = postService.savePost(post);

        return response.isSuccess() ?
                ResponseEntity.status(HttpStatus.CREATED).body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @GetMapping
    public ResponseEntity<?> getPostById(@RequestParam(name = "id") String postId) {
        var response = postService.getPostById(postId);

        return response.isSuccess() ?
                ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getAllPosts(@RequestParam(name = "channel") String channelId,
                                         @RequestParam(name = "personnel", required = false) boolean personnel) {
        var response = postService.getAllPosts(channelId, personnel);

        return response.isSuccess() ?
                ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @PostMapping("/reaction")
    public ResponseEntity<?> updateReaction(@Valid @RequestBody Reaction reaction, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiError(
                    HttpStatus.NOT_ACCEPTABLE,
                    HTTPResponseErrorFormatter.format(errors)
            ));
        }

        var response = postService.saveReaction(reaction);

        return response.isSuccess() ?
                ResponseEntity.status(HttpStatus.CREATED).body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }
}
