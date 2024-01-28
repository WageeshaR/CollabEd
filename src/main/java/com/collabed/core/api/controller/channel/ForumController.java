package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.service.channel.ForumService;
import com.collabed.core.service.util.CEServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("forums")
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @PostMapping
    public ResponseEntity<?> createForum(@Valid @RequestBody Forum forum, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HTTPResponseErrorFormatter.format(errors));

        CEServiceResponse response = forumService.createForum(forum);

        if (response.isSuccess())
            return ResponseEntity.status(HttpStatus.CREATED).build();

        return ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }

    @PutMapping("resolve/{id}")
    public ResponseEntity<?> resolveForum(@PathVariable(name = "id") String forumId) {
        CEServiceResponse response = forumService.resolveThread(forumId);

        if (response.isSuccess())
            return ResponseEntity.ok().build();

        return ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }
}
