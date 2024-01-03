package com.collabed.core.service;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.data.repository.channel.TopicRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.channel.ChannelService;
import com.collabed.core.service.util.CEServiceResponse;
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
    private TopicRepository topicRepository;

    @BeforeEach
    public void setup() {
        channelRepository = Mockito.mock(ChannelRepository.class);
        topicRepository = Mockito.mock(TopicRepository.class);
        channelService = new ChannelService(channelRepository, topicRepository);
    }

    @Test
    public void createChannelTest() {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.save(channel)).thenReturn(channel);
        CEServiceResponse response = channelService.saveChannel(channel);
        assertInstanceOf(Channel.class, response.getData());
    }

    @Test
    public void createChannelDuplicateErrorTest() {
        Channel channel1 = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.save(channel1)).thenThrow(DuplicateKeyException.class);
        CEServiceResponse response1 = channelService.saveChannel(channel1);
        assertTrue(response1.isError());
        assertEquals(
                response1.getMessage(),
                String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "channel")
        );

        Channel channel2 = Mockito.mock(Channel.class);
        Mockito.when(channelRepository.save(channel2)).thenThrow(com.mongodb.DuplicateKeyException.class);
        CEServiceResponse response2 = channelService.saveChannel(channel2);
        assertTrue(response2.isError());
        assertEquals(
                response2.getMessage(),
                String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "channel")
        );
    }

    @Test
    public void findChannelByIdTest() {
        String id = new ObjectId().toHexString();
        Channel channel = new Channel();
        channel.setId(id);
        Mockito.when(channelRepository.findById(Mockito.anyString())).thenReturn(Optional.of(channel));
        CEServiceResponse response = channelService.findChannelById(new ObjectId().toHexString());
        assertInstanceOf(Channel.class, response.getData());
        assertEquals(id, ((Channel) response.getData()).getId());
    }

    @Test
    public void findChannelByIdErrorTest() {
        String id1 = new ObjectId().toHexString();
        Mockito.when(channelRepository.findById(id1)).thenReturn(Optional.of(Mockito.mock(Channel.class)));
        CEServiceResponse response = channelService.findChannelById(Mockito.anyString());
        assertTrue(response.isError());
        assertEquals(
                response.getMessage(),
                String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
        );
    }

    @Test
    public void findChannelByNameTest() {
        String name = new Random().toString();
        Channel channel = new Channel();
        channel.setName(name);
        Mockito.when(channelRepository.findByName(name)).thenReturn(Optional.of(channel));
        CEServiceResponse response = channelService.findChannelByName(name);
        assertInstanceOf(Channel.class, response.getData());
        assertEquals(name, ((Channel) response.getData()).getName());
    }

    @Test
    public void findChannelByNameErrorTest() {
        String id1 = new ObjectId().toHexString();
        Mockito.when(channelRepository.findByName(id1)).thenReturn(Optional.of(Mockito.mock(Channel.class)));
        CEServiceResponse response = channelService.findChannelByName(Mockito.anyString());
        assertTrue(response.isError());
        assertEquals(
                response.getMessage(),
                String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    public void getAllChannelsTest(int num) {
        List<Channel> channels = new ArrayList<>();
        for (int i=0; i<num; i++) {
            channels.add(Mockito.mock(Channel.class));
        }
        Mockito.when(channelRepository.findAll()).thenReturn(channels);
        CEServiceResponse response = channelService.getAllChannels();
        assertInstanceOf(List.class, response.getData());
        assertInstanceOf(Channel.class, ((List<?>) response.getData()).get(0));
    }
    
    @Test
    public void getAllChannelsErrorTest() {
        Mockito.when(channelRepository.findAll()).thenThrow(MongoQueryException.class);
        CEServiceResponse response = channelService.getAllChannels();
        assertTrue(response.isError());
        assertEquals(
                response.getMessage(),
                String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    public void getAllChannelsByTopicTest(int num) {
        List<Channel> channels = new ArrayList<>();
        for (int i=0; i<num; i++) {
            channels.add(Mockito.mock(Channel.class));
        }
        Mockito.when(channelRepository.findAllByTopic(Mockito.anyString())).thenReturn(channels);
        CEServiceResponse response = channelService.getAllChannelsByTopic("dsfds78&834");
        assertInstanceOf(List.class, response.getData());
        assertInstanceOf(Channel.class, ((List<?>) response.getData()).get(0));
    }

    @Test
    public void getAllChannelsByTopicErrorTest() {
        Mockito.when(channelRepository.findAllByTopic(Mockito.anyString())).thenThrow(MongoQueryException.class);
        CEServiceResponse response = channelService.getAllChannelsByTopic("dsfds78&834");
        assertTrue(response.isError());
        assertEquals(
                response.getMessage(),
                String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "channel")
        );
    }
}
