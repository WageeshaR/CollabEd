package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.util.CEServiceResponse;
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

    public CEServiceResponse saveChannel(Channel channel) {
        try {
            Channel savedChannel = channelRepository.save(channel);
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
            List<Channel> channels = channelRepository.findAll();
            return CEServiceResponse.success().data(channels);
        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel")).data(e);
        }
    }

    public CEServiceResponse getAllChannelsByTopic(String topicName) {
        try {
            List<Channel> channels = channelRepository.findAllByTopic(topicName);
            return CEServiceResponse.success().data(channels);
        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel")).data(e);
        }
    }

    public CEServiceResponse findChannelById(String id) {
        try {
            Channel channel = channelRepository.findById(id).orElseThrow();
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
            Channel channel = channelRepository.findByName(name).orElseThrow();
            return CEServiceResponse.success().data(channel);
        } catch (NoSuchElementException e) {
            log.info(LoggingMessage.Error.NO_SUCH_ELEMENT + e);
            return CEServiceResponse.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")).data(e);
        } catch (RuntimeException e) {
            log.error(LoggingMessage.Error.SERVICE + e);
            return CEServiceResponse.error().data(e);
        }
    }
}
