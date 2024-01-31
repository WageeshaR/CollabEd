package com.collabed.core.service.channel;

import com.collabed.core.api.controller.channel.VisibilityEnum;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Topic;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.data.repository.channel.TopicRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.intel.CEIntelService;
import com.collabed.core.service.util.CEServiceResponse;
import com.collabed.core.util.LoggingMessage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Log4j2
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final TopicRepository topicRepository;
    private final CEIntelService intelService;

    public CEServiceResponse saveChannel(Channel channel) {
        try {
            var savedChannel = channelRepository.save(channel);
            log.info(LoggingMessage.Success.SAVE);
            return CEServiceResponse.success().data(savedChannel);

        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            log.error(LoggingMessage.Error.DUPLICATE_KEY + e);
            return CEServiceResponse
                    .error(String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "channel"))
                    .data(e);

        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse getAllChannels() {
        try {
            var channels = channelRepository.findAll();
            return CEServiceResponse.success().data(channels);

        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel")).data(e);
        }
    }

    public CEServiceResponse getAllChannelsByTopic(String topicName) {
        try {
            var channels = channelRepository.findAllByTopic(topicName);
            return CEServiceResponse.success().data(channels);

        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel")).data(e);
        }
    }

    public CEServiceResponse findChannelById(String id) {
        try {
            var channel = channelRepository.findById(id).orElseThrow();
            return CEServiceResponse.success().data(channel);

        } catch (NoSuchElementException e) {
            log.info(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse
                    .error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")).data(e);

        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse findChannelByName(String name) {
        try {
            var channel = channelRepository.findByName(name).orElseThrow();
            return CEServiceResponse.success().data(channel);

        } catch (NoSuchElementException e) {
            log.info(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")).data(e);

        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse deleteChannel(String id) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            channelRepository.updateAndSoftDelete(id, user.getUsername());
            log.info(String.format("Channel %s deleted successfully.", id));
            return CEServiceResponse.success().build();

        } catch (NoSuchElementException e) {
            log.error(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel created by " + user.getUsername())
            ).build();

        } catch (RuntimeException e) {
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse changeVisibility(String channelId, VisibilityEnum visibility) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            int updated = channelRepository.updateVisibility(channelId, user.getUsername(), visibility);
            if (updated == 0)
                throw new NoSuchElementException();

            log.info(String.format("Visibility of channel %s updated successfully", channelId));
            return CEServiceResponse.success().build();

        } catch (NoSuchElementException e) {
            log.error(String.format(LoggingMessage.Error.ILLEGAL_MODIFICATION, "channel " + channelId, user.getId()));
            return CEServiceResponse.error(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel and owner combination")
            ).data(e);

        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse topics() {
        try {
            List<Topic> allTopics = topicRepository.findAll();

            return CEServiceResponse.success().data(allTopics);
        } catch (Exception e) {
            log.error(LoggingMessage.Error.SERVICE + e);

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "topic")
            ).data(e);
        }
    }

    public CEServiceResponse curatedUserChannels() {
        try {
            boolean setup = intelService.setupGateway();

            if (setup) {
                List<?> curatedList = intelService.getCuratedListOfType(Channel.class);
                if (!curatedList.isEmpty())
                    return CEServiceResponse.success().data(curatedList);

                return CEServiceResponse.error(
                        String.format(CEInternalErrorMessage.SERVICE_OPERATION_FAILED, "Intel", "fetch curated list of channels")
                ).build();
            }

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.GATEWAY_OPERATION_FAILED, "setup", "intel")
            ).build();

        } catch (Exception e) {
            log.error(e);
            return CEServiceResponse.error().data(e);
        }
    }
}
