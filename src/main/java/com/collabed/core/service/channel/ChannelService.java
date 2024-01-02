package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.util.LoggingMessage;
import com.mongodb.MongoException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Log4j2
public class ChannelService {
    private final ChannelRepository channelRepository;

    public Channel saveChannel(Channel channel) {
        try {
            Channel savedChannel = channelRepository.save(channel);
            log.info(LoggingMessage.Success.SAVE);
            return savedChannel;
        } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            log.error(LoggingMessage.Error.DUPLICATE_KEY + e);
            throw new CEWebRequestError(
                    String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "channel")
            );
        } catch (MongoException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            throw new CEServiceError(
                    String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "channel")
            );
        }
    }

    public List<Channel> getAllChannels() {
        try {
            return channelRepository.findAll();
        } catch (MongoException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }

    public List<Channel> getAllChannelsByTopic(String topicName) {
        try {
            return channelRepository.findAllByTopic(topicName);
        } catch (MongoException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }

    public Channel findChannelById(String id) {
        try {
            return channelRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            log.info(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            throw new CEWebRequestError(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
            );
        } catch (MongoException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }

    public Channel findChannelByName(String name) {
        try {
            return channelRepository.findByName(name).orElseThrow();
        } catch (NoSuchElementException e) {
            log.info(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            throw new CEWebRequestError(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel"));
        } catch (MongoException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            throw new CEServiceError(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel"));
        }
    }
}
