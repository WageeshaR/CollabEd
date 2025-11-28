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
import com.mongodb.MongoQueryException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

import static com.collabed.core.service.util.SecurityUtil.withAuthentication;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTests {
    @InjectMocks
    private ChannelService channelService;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private CEIntelService intelService;

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
        Mockito.when(channelRepository.findById(Mockito.anyString())).thenThrow(NoSuchElementException.class);
        CEServiceResponse response = channelService.findChannelById(new ObjectId().toHexString());
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
        Mockito.when(channelRepository.findByName(Mockito.anyString())).thenThrow(NoSuchElementException.class);
        CEServiceResponse response = channelService.findChannelByName("testChannelName");
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

    @Test
    public void deleteChannelTest() {
        User user = new User();
        user.setUsername("username");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.doNothing().when(channelRepository).updateAndSoftDelete("myrandomid", "username");
        CEServiceResponse response = channelService.deleteChannel("myrandomid");
        assertTrue(response.isSuccess());
    }

    @Test
    public void deleteChannelNoSuchElementErrorTest() {
        User user = new User();
        user.setUsername("username");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.doThrow(NoSuchElementException.class).when(channelRepository).updateAndSoftDelete("myrandomid", "username");
        CEServiceResponse response = channelService.deleteChannel("myrandomid");
        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel created by " + user.getUsername()));
    }

    @Test
    public void changeVisibilityTest() {
        User user = new User();
        user.setUsername("username");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.when(channelRepository.updateVisibility("myrandomchannelid", user.getUsername(), VisibilityEnum.PUBLIC)).thenReturn(1);

        CEServiceResponse response = channelService.changeVisibility("myrandomchannelid", VisibilityEnum.PUBLIC);

        assertTrue(response.isSuccess());

    }

    @Test
    public void changeVisibilityErrorTest() {
        User user = new User();
        user.setUsername("username");
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);

        Mockito.when(channelRepository.updateVisibility("myrandomchannelid", user.getUsername(), VisibilityEnum.PUBLIC)).thenThrow(NoSuchElementException.class);

        CEServiceResponse response = channelService.changeVisibility("myrandomchannelid", VisibilityEnum.PUBLIC);

        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel and owner combination"));
    }

    @ParameterizedTest
    @ValueSource(ints = {3,10,50})
    public void topicsTest(int count) {
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i <count ; i++) {
            topics.add(Mockito.mock(Topic.class));
        }

        Mockito.when(topicRepository.findAll()).thenReturn(topics);

        CEServiceResponse response = channelService.topics();

        assertTrue(response.isSuccess());
        assertEquals(((List<?>)response.getData()).size(), topics.size());
    }

    @Test
    public void topicsErrorTest() {
        Mockito.when(topicRepository.findAll()).thenThrow(MongoQueryException.class);

        CEServiceResponse response = channelService.topics();
        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "topic"));
    }

    @ParameterizedTest
    @ValueSource(ints = {3,10,50})
    public void curatedUserChannelsTest(int count) {
        Mockito.when(intelService.setupGateway()).thenReturn(true);

        List<Channel> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(Mockito.mock(Channel.class));
        }

        Mockito.doReturn(list).when(intelService).getCuratedListOfType(Channel.class);

        CEServiceResponse response = channelService.curatedUserChannels();

        assertTrue(response.isSuccess());
        assertEquals(((List<?>)response.getData()).size(), count);
    }

    @Test
    public void curatedUserChannelsSetupErrorTest() {
        Mockito.when(intelService.setupGateway()).thenReturn(false);

        CEServiceResponse response = channelService.curatedUserChannels();
        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEInternalErrorMessage.GATEWAY_OPERATION_FAILED, "setup", "intel"));
    }

    @Test
    public void curatedUserChannelsEmptyResultsErrorTest() {
        Mockito.when(intelService.setupGateway()).thenReturn(true);
        Mockito.doReturn(List.of()).when(intelService).getCuratedListOfType(Channel.class);

        CEServiceResponse response = channelService.curatedUserChannels();
        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEInternalErrorMessage.SERVICE_OPERATION_FAILED, "Intel", "fetch curated list of channels"));
    }
}