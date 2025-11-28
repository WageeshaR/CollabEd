package com.collabed.core.api.contorller.channel;


import static com.collabed.core.util.HttpRequestResponseUtils.isApiError;
import static com.collabed.core.util.HttpRequestResponseUtils.mapToJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.collabed.core.api.controller.channel.PostController;
import com.collabed.core.api.util.JwtTokenUtil;
import com.collabed.core.config.SecurityConfig;
import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.PostContent;
import com.collabed.core.data.model.channel.Reaction;
import com.collabed.core.data.repository.channel.ChannelRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.UserService;
import com.collabed.core.service.channel.PostService;
import com.collabed.core.service.util.CEServiceResponse;
import java.util.List;
import java.util.Optional;
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

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private UserService userService;
    @MockBean
    private PostService postService;
    @MockBean
    private ChannelRepository channelRepository;

    @Test
    @WithMockUser
    void savePostTest() throws Exception {
        Channel channel = new Channel();
        channel.setId(new ObjectId().toHexString());
        Post post = new Post();
        post.setTitle("Example title");
        post.setChannel(channel);
        post.setContent(Mockito.mock(PostContent.class));

        Mockito.when(channelRepository.findById(Mockito.anyString())).thenReturn(Optional.of(channel));
        Mockito.when(postService.savePost(Mockito.any(Post.class))).thenReturn(CEServiceResponse.success().data(post));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/posts")
                .content(mapToJson(post))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Example title"));
    }

    @Test
    @WithMockUser
    void savePostNoChannelIdTest() throws Exception {
        Channel channel = new Channel();
        Post post = new Post();
        post.setTitle("Example title");
        post.setChannel(channel);
        post.setContent(Mockito.mock(PostContent.class));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/posts")
                .content(mapToJson(post))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError(
                        String.format(CEUserErrorMessage.ENTITY_PROPERTY_MUST_NOT_BE_NULL, "channel", "id")
                )));
    }

    @Test
    @WithMockUser
    void savePostChannelNotFoundTest() throws Exception {
        Channel channel = new Channel();
        channel.setId(new ObjectId().toHexString());
        Post post = new Post();
        post.setTitle("Example title");
        post.setChannel(channel);
        post.setContent(Mockito.mock(PostContent.class));

        Mockito.when(channelRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/posts")
                        .content(mapToJson(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError(
                        String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "channel")
                )));
    }

    @Test
    @WithMockUser
    void savePostExceptionTest() throws Exception {
        Channel channel = new Channel();
        channel.setId(new ObjectId().toHexString());
        Post post = new Post();
        post.setTitle("Example title");
        post.setChannel(channel);
        post.setContent(Mockito.mock(PostContent.class));

        Mockito.when(channelRepository.findById(Mockito.anyString())).thenReturn(Optional.of(channel));
        Mockito.when(postService.savePost(Mockito.any(Post.class))).thenReturn(CEServiceResponse.error(
                String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "post")
        ).build());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/posts")
                        .content(mapToJson(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError(
                        String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "post")
                )));
    }

    @Test
    @WithMockUser
    void getPostByIdTest() throws Exception {
        ObjectId oid = new ObjectId();
        Post post = new Post();
        post.setId(oid.toHexString());
        Mockito.when(postService.getPostById(Mockito.anyString()))
                .thenReturn(CEServiceResponse.success().data(post));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/posts")
                .param("id", "myrandompostid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(oid.toHexString()));
    }

    @Test
    @WithMockUser
    void getPostByIdErrorTest() throws Exception {
        Mockito.when(postService.getPostById(Mockito.anyString()))
                .thenReturn(CEServiceResponse.error().build());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/posts")
                .param("id", "myrandompostid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void getAllPostsTest() throws Exception {
        Mockito.when(postService.getAllPosts(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(
                CEServiceResponse.success().data(List.of())
        );

        mockMvc.perform(MockMvcRequestBuilders
                .get("/posts/filter")
                .param("channel", "myrandompostid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void getAllPostsErrorTest() throws Exception {
        Mockito.when(postService.getAllPosts(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(
                CEServiceResponse.error().build()
        );

        mockMvc.perform(MockMvcRequestBuilders
                .get("/posts/filter")
                .param("channel", "myrandompostid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }

    @Test
    @WithMockUser
    void updateReactionTest() throws Exception {
        Reaction reaction = new Reaction();
        reaction.setEmoji("heart_emoji");
        reaction.setPost(Mockito.mock(Post.class));

        Mockito.when(postService.saveReaction(Mockito.any(Reaction.class))).thenReturn(CEServiceResponse.success().data(reaction));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/posts/reaction")
                .content(mapToJson(reaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.emoji").value("heart_emoji"));
    }

    @Test
    @WithMockUser
    void updateReactionInvalidDataTest() throws  Exception {
        Reaction reaction = Mockito.mock(Reaction.class);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/posts/reaction")
                .content(mapToJson(reaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError(
                        "[post: must not be null]"
                )));
    }

    @Test
    @WithMockUser
    void updateReactionErrorTest() throws Exception {
        Reaction reaction = new Reaction();
        reaction.setEmoji("heart_emoji");
        reaction.setPost(Mockito.mock(Post.class));

        Mockito.when(postService.saveReaction(Mockito.any(Reaction.class))).thenReturn(CEServiceResponse.error().build());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/posts/reaction")
                        .content(mapToJson(reaction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(isApiError()));
    }
}
