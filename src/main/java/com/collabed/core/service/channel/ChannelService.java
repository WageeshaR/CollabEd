package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.mongodb.MongoException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    public Channel createChannel(Channel channel) {
        try {
            return channelRepository.insert(channel);
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new CEWebRequestError(
                    String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "channel")
            );
        } catch (MongoException e) {
            throw new CEServiceError(
                    String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "channel")
            );
        }
    }

    public List<Channel> getAllChannels() {
        try {
            return channelRepository.findAll();
        } catch (MongoException e) {
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }

    public List<Channel> getAllChannelsByTopic(String topicName) {
        try {
            return channelRepository.findAllByTopic(topicName);
        } catch (MongoException e) {
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }

    public Channel findChannelById(String id) {
        try {
            return channelRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
            );
        } catch (MongoException e) {
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }

    public Channel findChannelByName(String name) {
        try {
            return channelRepository.findByName(name).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel"));
        } catch (MongoException e) {
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }
}
