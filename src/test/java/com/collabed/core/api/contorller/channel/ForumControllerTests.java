package com.collabed.core.api.contorller.channel;

import com.collabed.core.api.controller.channel.ForumController;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.model.channel.Thread;
import com.collabed.core.data.model.user.User;
import com.collabed.core.service.UserService;
import com.collabed.core.service.channel.ForumService;
import com.collabed.core.service.util.CEServiceResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.collabed.core.util.HttpRequestResponseUtils.countMatcher;
import static com.collabed.core.util.HttpRequestResponseUtils.isApiError;
import static com.collabed.core.util.HttpRequestResponseUtils.mapToJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForumController.class)
@Import(SecurityConfig.class)
public class ForumControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private UserService userService;
    @MockBean
    private ForumService forumService;

    @Test
    @WithMockUser
    public void createForumTest() throws Exception {
        Forum forum = new Forum();
        forum.setChannel(Mockito.mock(Channel.class));

        Mockito.when(forumService.createForum(Mockito.any(Forum.class))).thenReturn(CEServiceResponse.success().data(forum));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/forums")
                .content(mapToJson(forum))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void createForumErrorTest() throws Exception {
        Forum forum = new Forum();
        forum.setChannel(Mockito.mock(Channel.class));

        Mockito.when(forumService.createForum(Mockito.any(Forum.class))).thenReturn(CEServiceResponse.error().build());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/forums")
                .content(mapToJson(forum))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }

    @Test
    @WithMockUser
    public void createThreadTest() throws Exception {
        Thread thread = new Thread();
        thread.setId(new ObjectId().toHexString());
        thread.setForum(Mockito.mock(Forum.class));
        thread.setSubject("Test thread subject");
        thread.setMembers(List.of(Mockito.mock(User.class), Mockito.mock(User.class)));

        Mockito.when(forumService.createThread(Mockito.any(Thread.class))).thenReturn(CEServiceResponse.success().data(thread));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/forums/thread")
                .content(mapToJson(thread))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(thread.getId()));

    }

    @Test
    @WithMockUser
    public void resolveThreadTest() throws Exception {

        Mockito.when(forumService.resolveThread(Mockito.anyString())).thenReturn(CEServiceResponse.success().build());

        mockMvc.perform(MockMvcRequestBuilders
                .put("/forums/resolve/{id}", new ObjectId().toHexString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void resolveThreadErrorTest() throws Exception {

        Mockito.when(forumService.resolveThread(Mockito.anyString())).thenReturn(CEServiceResponse.error().build());

        mockMvc.perform(MockMvcRequestBuilders
                .put("/forums/resolve/{id}", new ObjectId().toHexString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }

    @Test
    @WithMockUser
    public void addParticipantsToThreadTest() throws Exception {
        User user = new User();
        user.setId(new ObjectId().toHexString());

        Thread thread = new Thread();
        thread.setId(new ObjectId().toHexString());
        thread.addMember(user);
        
        Mockito.when(forumService.addUserToThread(Mockito.anyString(), Mockito.anyString())).thenReturn(CEServiceResponse.success().data(thread));

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("thread", thread.getId());
        requestMap.put("user", user.getId());

        mockMvc.perform(MockMvcRequestBuilders
            .post("/forums/add_participant")
            .content(mapToJson(requestMap))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.members").value(countMatcher(1)));
    }

    @Test
    @WithMockUser
    public void addParticipantNoThreadErrorTest() throws Exception {

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("user", new ObjectId().toHexString());

        mockMvc.perform(MockMvcRequestBuilders
            .post("/forums/add_participant")
            .content(mapToJson(requestMap))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$").value("thread must not be empty"));

    }

    @Test
    @WithMockUser
    public void addParticipantNoUserErrorTest() throws Exception {

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("thread", new ObjectId().toHexString());

        mockMvc.perform(MockMvcRequestBuilders
            .post("/forums/add_participant")
            .content(mapToJson(requestMap))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$").value("user must not be empty"));

    }

    @Test
    @WithMockUser
    public void addParticipantsToThreadErrorTest() throws Exception {

        Mockito.when(forumService.addUserToThread(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(CEServiceResponse.error().data(new RuntimeException()));

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("thread", new ObjectId().toHexString());
        requestMap.put("user", new ObjectId().toHexString());

        mockMvc.perform(MockMvcRequestBuilders
            .post("/forums/add_participant")
            .content(mapToJson(requestMap))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }
}
