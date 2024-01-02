package com.collabed.core.api.contorller.channel;

import com.collabed.core.api.controller.channel.ChannelController;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Topic;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.UserService;
import com.collabed.core.service.channel.ChannelService;
import com.collabed.core.service.util.CEServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.collabed.core.util.HttpRequestResponseUtils.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import(SecurityConfig.class)
public class ChannelControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private UserService userService;
    @MockBean
    private ChannelService channelService;

    @Test
    @WithMockUser
    public void createChannelTest() throws Exception {
        Channel channel = new Channel();
        channel.setName("My Channel");
        channel.setTopic(Mockito.mock(Topic.class));
        Mockito.when(channelService.saveChannel(Mockito.any(Channel.class))).thenReturn(
                CEServiceResponse.success().data(channel)
        );
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/channels")
                    .content(mapToJson(channel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("My Channel"));
    }

    @Test
    @WithMockUser
    public void createChannelInvalidDataTest() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/channels")
                    .content(mapToJson(channel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(constraintValidationMessageMatcher(
                        List.of("name: must not be null", "topic: must not be null")
                )));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    @WithMockUser
    public void getAllChannelsTest(int num) throws Exception {
        List<Channel> channels = new ArrayList<>();
        for (int i=0; i<num; i++)
            channels.add(Mockito.mock(Channel.class));
        Mockito.when(channelService.getAllChannels()).thenReturn(
                CEServiceResponse.success().data(channels)
        );
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/channels")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(countMatcher(num)));
    }

    @Test
    @WithMockUser
    public void getAllChannelsErrorTest() throws Exception {
        Mockito.when(channelService.getAllChannels()).thenReturn(
                CEServiceResponse.error().data(new RuntimeException())
        );
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/channels")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }

    @Test
    @WithMockUser
    public void filterByIdTest() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channelService.findChannelById("myid")).thenReturn(
                CEServiceResponse.success().data(channel)
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("id", "myid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser
    public void filterByIdErrorTest() throws Exception {
        Mockito.when(channelService.findChannelById("myid1")).thenReturn(
                CEServiceResponse.error().data(new CEServiceError(""))
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("id", "myid1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));

        Mockito.when(channelService.findChannelById("myid2")).thenReturn(
                CEServiceResponse.error().data(new CEWebRequestError(""))
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("id", "myid2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }

    @Test
    @WithMockUser
    public void filterByaNameTest() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channelService.findChannelByName("myname")).thenReturn(
                CEServiceResponse.success().data(channel)
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("name", "myname")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser
    public void filterByNameErrorTest() throws Exception {
        Mockito.when(channelService.findChannelByName("myname1")).thenReturn(
                CEServiceResponse.error().data(new CEServiceError(""))
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("name", "myname1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));

        Mockito.when(channelService.findChannelByName("myname2")).thenReturn(
                CEServiceResponse.error().data(new CEWebRequestError(""))
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("name", "myname2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    @WithMockUser
    public void filterByTopicTest(int num) throws Exception {
        List<Channel> channels = new ArrayList<>();
        for (int i=0; i<num; i++)
            channels.add(Mockito.mock(Channel.class));
        Mockito.when(channelService.getAllChannelsByTopic("mytopic")).thenReturn(
                CEServiceResponse.success().data(channels)
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("topic", "mytopic")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(countMatcher(num)));
    }

    @Test
    @WithMockUser
    public void filterByTopicErrorTest() throws Exception {
        Mockito.when(channelService.getAllChannelsByTopic("mytopic")).thenReturn(
                CEServiceResponse.error().data(new CEServiceError(""))
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/channels/filter")
                        .queryParam("topic", "mytopic")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }
}
