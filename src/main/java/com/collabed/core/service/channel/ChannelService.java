package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Topic;
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
            throw new CEWebRequestError(CEUserErrorMessage.DUPLICATE_CHANNEL_ENTRIES);
        } catch (MongoException e) {
            throw new CEServiceError(CEInternalErrorMessage.CHANNEL_SERVICE_UPDATE_FAILED);
        }
    }

    public Channel findChannelById(String id) {
        try {
            return channelRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(CEUserErrorMessage.CHANNEL_NOT_EXISTS);
        } catch (MongoException e) {
            throw new CEServiceError(CEInternalErrorMessage.CHANNEL_SERVICE_QUERY_FAILED);
        }
    }

    public Channel findChannelByName(String name) {
        try {
            return channelRepository.findByName(name).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new CEWebRequestError(CEUserErrorMessage.CHANNEL_NOT_EXISTS);
        } catch (MongoException e) {
            throw new CEServiceError(CEInternalErrorMessage.CHANNEL_SERVICE_QUERY_FAILED);
        }
    }

    public List<Channel> getAllChannels() {
        try {
            return channelRepository.findAll();
        } catch (MongoException e) {
            throw new CEServiceError(CEInternalErrorMessage.CHANNEL_SERVICE_QUERY_FAILED);
        }
    }

    public List<Channel> getAllChannelsByTopic(Topic topic) {
        try {
            return channelRepository.findAllByTopic(topic);
        } catch (MongoException e) {
            throw new CEServiceError(CEInternalErrorMessage.CHANNEL_SERVICE_QUERY_FAILED);
        }
    }
}
