package com.collabed.core.service;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Topic;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.channel.ChannelService;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ChannelServiceTests {
    private ChannelService channelService;
    private ChannelRepository channelRepository;

    @BeforeEach
    public void setup() {
        channelRepository = Mockito.mock(ChannelRepository.class);
        channelService = new ChannelService(channelRepository);
    }

    @Test
    public void createChannelTest() {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.insert(channel)).thenReturn(channel);
        Object result = channelService.createChannel(channel);
        assertInstanceOf(Channel.class, result);
    }

    @Test
    public void createChannelDuplicateErrorTest() {
        Channel channel1 = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.insert(channel1)).thenThrow(DuplicateKeyException.class);
        Exception error1 = assertThrows(CEWebRequestError.class, () -> channelService.createChannel(channel1));
        assertEquals(error1.getMessage(), CEUserErrorMessage.DUPLICATE_CHANNEL_ENTRIES);

        Channel channel2 = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.insert(channel2)).thenThrow(com.mongodb.DuplicateKeyException.class);
        Exception error2 = assertThrows(CEWebRequestError.class, () -> channelService.createChannel(channel2));
        assertEquals(error2.getMessage(), CEUserErrorMessage.DUPLICATE_CHANNEL_ENTRIES);

        Channel channel3 = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.insert(channel3)).thenThrow(MongoException.class);
        assertThrows(CEServiceError.class, () -> channelService.createChannel(channel3));
    }

    @Test
    public void findChannelByIdTest() {
        Mockito.when(channelRepository.findById(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(Channel.class)));
        Object result = channelService.findChannelById(new ObjectId().toHexString());
        assertInstanceOf(Channel.class, result);
    }

    @Test
    public void findChannelByIdErrorTest() {
        String id1 = new ObjectId().toHexString();
        Mockito.when(channelRepository.findById(id1)).thenReturn(Optional.of(Mockito.mock(Channel.class)));
        Exception error = assertThrows(CEWebRequestError.class, () -> channelService.findChannelById(Mockito.anyString()));
        assertEquals(error.getMessage(), CEUserErrorMessage.CHANNEL_NOT_EXISTS);

        String id2 = new ObjectId().toHexString();
        Mockito.when(channelRepository.findById(Mockito.anyString())).thenThrow(MongoException.class);
        assertThrows(CEServiceError.class, () -> channelService.findChannelById(id2));
    }

    @Test
    public void findChannelByNameTest() {
        String name = new Random().toString();
        Mockito.when(channelRepository.findByName(name)).thenReturn(Optional.of(Mockito.mock(Channel.class)));
        Object result = channelService.findChannelByName(name);
        assertInstanceOf(Channel.class, result);
    }

    @Test
    public void findChannelByNameErrorTest() {
        String id1 = new ObjectId().toHexString();
        Mockito.when(channelRepository.findByName(id1)).thenReturn(Optional.of(Mockito.mock(Channel.class)));
        Exception error = assertThrows(CEWebRequestError.class, () -> channelService.findChannelByName(Mockito.anyString()));
        assertEquals(error.getMessage(), CEUserErrorMessage.CHANNEL_NOT_EXISTS);

        String id2 = new ObjectId().toHexString();
        Mockito.when(channelRepository.findById(Mockito.anyString())).thenThrow(MongoException.class);
        assertThrows(CEServiceError.class, () -> channelService.findChannelById(id2));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    public void getAllChannelsTest(int num) {
        List<Channel> channels = new ArrayList<>();
        for (int i=0; i<num; i++) {
            channels.add(Mockito.mock(Channel.class));
        }
        Mockito.when(channelRepository.findAll()).thenReturn(channels);
        Object result = channelService.getAllChannels();
        assertInstanceOf(List.class, result);
    }
    
    @Test
    public void getAllChannelsErrorTest() {
        Mockito.when(channelRepository.findAll()).thenThrow(MongoQueryException.class);
        Exception error = assertThrows(CEServiceError.class, () -> channelService.getAllChannels());
        assertEquals(error.getMessage(), CEInternalErrorMessage.CHANNEL_SERVICE_QUERY_FAILED);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    public void getAllChannelsByTopicTest(int num) {
        List<Channel> channels = new ArrayList<>();
        for (int i=0; i<num; i++) {
            channels.add(Mockito.mock(Channel.class));
        }
        Mockito.when(channelRepository.findAllByTopic(Mockito.anyString())).thenReturn(channels);
        Object result = channelService.getAllChannelsByTopic("dsfds78&834");
        assertInstanceOf(List.class, result);
    }

    @Test
    public void getAllChannelsByTopicErrorTest() {
        Mockito.when(channelRepository.findAllByTopic(Mockito.anyString())).thenThrow(MongoQueryException.class);
        Exception error = assertThrows(CEServiceError.class, () -> channelService.getAllChannelsByTopic("dsfds78&834"));
        assertEquals(error.getMessage(), CEInternalErrorMessage.CHANNEL_SERVICE_QUERY_FAILED);
    }
}
