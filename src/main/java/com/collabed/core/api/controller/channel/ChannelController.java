package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.user.User;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.channel.ChannelService;
import com.collabed.core.service.util.CEServiceResponse;
import com.collabed.core.util.LoggingMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@RestController
@RequestMapping("channels")
@AllArgsConstructor
@Log4j2
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<?> createChannel(@Valid @RequestBody Channel channel, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    HTTPResponseErrorFormatter.format(errors)
            ));
        }

        var response = channelService.saveChannel(channel);

        return response.isSuccess() ?

                ResponseEntity.ok().body(response.getData()) :

                ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    response.getMessage(),
                    (Exception) response.getData()
        ));
    }

    @PutMapping("/change_visibility")
    public ResponseEntity<?> changeVisibility(@RequestBody Channel channel) {
        if (channel.getId() == null)
            return ResponseEntity.badRequest().body("[id: must not be null]");

        if (channel.getVisibility() == null)
            return ResponseEntity.badRequest().body("[visibility: must not be null]");

        var response = channelService.changeVisibility(channel.getId(), channel.getVisibility());

        return response.isSuccess() ?
                ResponseEntity.ok().build() : ResponseEntity.internalServerError().body(
                        new ApiError(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                response.getMessage(),
                                (Exception) response.getData()
                        )
        );
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        var response = channelService.getAllChannels();

        return response.isSuccess() ?
                ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    response.getMessage(),
                    (Exception) response.getData()
                )
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterBy(
            @RequestParam(required = false, name = "id") String id,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "topic") String topic
    ) {
        CEServiceResponse response = null;

        if (id != null)
            response = channelService.findChannelById(id);
        if (name != null)
            response = channelService.findChannelByName(name);
        if (topic != null)
            response = channelService.getAllChannelsByTopic(topic);

        if (response != null) {
            return response.isSuccess() ?
                    ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(new ApiError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            response.getMessage(),
                            (Exception) response.getData()
                    )
            );
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteChannel(@RequestBody Channel channel) {
        if (channel.getId() == null)
            return ResponseEntity.badRequest().body("id: must not be null");

        var response = channelService.deleteChannel(channel.getId());

        return response.isSuccess() ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().body(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getMessage(),
                        (Exception) response.getData()
                )
        );
    }

    @GetMapping("/topics")
    public ResponseEntity<?> getAllTopics() {
        var response = channelService.topics();

        return response.isSuccess() ?
                ResponseEntity.ok().body(response.getData()) : ResponseEntity.internalServerError().body(
                        new ApiError(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                response.getMessage(),
                                (Exception) response.getData()
                        )
        );
    }

    @GetMapping("/curated")
    public ResponseEntity<?> getCuratedChannels() {
        var response = channelService.curatedUserChannels();

        if (response.isSuccess())
            return ResponseEntity.ok().body(response.getData());

        return ResponseEntity.internalServerError().body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getMessage(),
                (Exception) response.getData()
        ));
    }
}
