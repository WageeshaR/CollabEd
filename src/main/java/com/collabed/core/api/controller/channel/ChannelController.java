package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.ApiError;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.channel.ChannelService;
import com.collabed.core.util.LoggingMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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
        try {
            return ResponseEntity.ok().body(channelService.saveChannel(channel));
        } catch (CEWebRequestError e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            ));
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok().body(channelService.getAllChannels());
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            ));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterBy(
            @RequestParam(required = false, name = "id") String id,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "topic") String topic
    ) {
        try {
            if (id != null)
                return ResponseEntity.ok().body(channelService.findChannelById(id));
            if (name != null)
                return ResponseEntity.ok().body(channelService.findChannelByName(name));
            if (topic != null)
                return ResponseEntity.ok().body(channelService.getAllChannelsByTopic(topic));
        } catch (CEWebRequestError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
                    HttpStatus.NOT_FOUND,
                    e
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            ));
        }
        log.info(String.format(CEUserErrorMessage.NO_MATCHING_ELEMENTS_FOUND, "channels"));
        return ResponseEntity.ok().build();
    }
}
