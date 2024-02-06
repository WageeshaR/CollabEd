package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.model.channel.Thread;
import com.collabed.core.service.channel.ForumService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@RestController
@RequestMapping("forums")
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @PostMapping
    public ResponseEntity<?> createForum(@Valid @RequestBody Forum forum, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));

        var response = forumService.createForum(forum);

        if (response.isSuccess())
            return ResponseEntity.status(HttpStatus.CREATED).build();

        return ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @PostMapping("/thread")
    public ResponseEntity<?> createThread(@Valid @RequestBody Thread thread, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));

        var response = forumService.createThread(thread);

        if (response.isSuccess())
            return ResponseEntity.status(HttpStatus.CREATED).body(response.getData());

        return ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @PutMapping("resolve/{id}")
    public ResponseEntity<?> resolveForum(@PathVariable(name = "id") String forumId) {
        var response = forumService.resolveThread(forumId);

        if (response.isSuccess())
            return ResponseEntity.ok().build();

        return ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @PostMapping("add_participant")
    public ResponseEntity<?> addParticipantToThread(@RequestBody Map<String, String> request) {
        if (!request.containsKey("thread") || request.get("thread") == null || request.get("thread").length() == 0) {
            return ResponseEntity.badRequest().body("thread must not be empty");
        }

        if (!request.containsKey("user") || request.get("user") == null || request.get("user").length() == 0) {
            return ResponseEntity.badRequest().body("user must not be empty");
        }

        CEServiceResponse response = forumService.addUserToThread(request.get("thread"), request.get("user"));

        if (response.isSuccess())
            return ResponseEntity.ok().body(response.getData());
        return ResponseEntity.internalServerError().body(new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            response.getMessage(),
            (Exception) response.getData()
        ));
    }
}
