package com.collabed.core.api.controller.channel;

import com.collabed.core.api.util.HTTPResponseErrorFormatter;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("channels")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<?> createChannel(@Valid @RequestBody Channel channel, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HTTPResponseErrorFormatter.format(errors));
        }
        try {
            return ResponseEntity.ok().body(channelService.saveChannel(channel));
        } catch (CEWebRequestError e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok().body(channelService.getAllChannels());
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        } catch (CEServiceError e) {
            return ResponseEntity.internalServerError().body(e);
        }
        return ResponseEntity.ok().build();
    }
}
