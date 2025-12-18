package com.collabed.core.service.channel;

import static com.collabed.core.service.util.SecurityUtil.withAuthentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.Reaction;
import com.collabed.core.data.model.user.User;
import com.collabed.core.data.proxy.PostProxy;
import com.collabed.core.data.repository.channel.PostRepository;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class PostServiceTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @InjectMocks
    private PostService postService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getAllPostsTest(boolean personnel) {
        User user = Mockito.mock(User.class);
        Page<?> postPage = Mockito.mock(Page.class);

        if (personnel) {
            Mockito.when(withAuthentication().getPrincipal()).thenReturn(user);
            Mockito.doReturn(postPage).when(postRepository)
                    .findAllByAuthorAndChannelId(Mockito.any(User.class), Mockito.anyString(), Mockito.any(Pageable.class));
        }
        else
            Mockito.doReturn(postPage).when(postRepository)
                    .findAllByChannelId(Mockito.anyString(), Mockito.any(Pageable.class));

        CEServiceResponse response = postService.getAllPosts("myrandomchannelid", personnel);
        assertTrue(response.isSuccess());
        assertInstanceOf(List.class, response.getData());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getAllPostsErrorTest(boolean personnel) {
        if (personnel) {
            Mockito.when(withAuthentication().getPrincipal()).thenReturn(Mockito.mock(User.class));
            Mockito.doThrow(NoSuchElementException.class).when(postRepository)
                    .findAllByAuthorAndChannelId(Mockito.any(User.class), Mockito.anyString(), Mockito.any(Pageable.class));
        }
        else
            Mockito.doThrow(NoSuchElementException.class).when(postRepository)
                .findAllByChannelId(Mockito.anyString(), Mockito.any(Pageable.class));

        CEServiceResponse response = postService.getAllPosts("myrandomchannelid", personnel);
        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEUserErrorMessage.NO_MATCHING_ELEMENTS_FOUND, "posts"));
    }

    @Test
    void getPostByIdTest() {
        Post post = new Post();

        User author = new User();
        User randomUser = new User();

        author.setUsername("author");
        randomUser.setUsername("randomuser");

        post.setAuthor(author);

        // test running using user == author
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(author);
        Mockito.when(postRepository.findById(Mockito.anyString())).thenReturn(Optional.of(post));

        CEServiceResponse response1 = postService.getPostById("myrandompostid");
        assertTrue(response1.isSuccess());
        assertInstanceOf(Post.class, response1.getData());

        // test running using user != author
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(randomUser);
        Mockito.when(postRepository.findById(Mockito.anyString())).thenReturn(Optional.of(post));

        CEServiceResponse response2 = postService.getPostById("myrandompostid");
        assertTrue(response2.isSuccess());
        assertInstanceOf(PostProxy.class, response2.getData());
    }

    @Test
    void getPostByIdErrorTest() {
        Mockito.when(postRepository.findById(Mockito.anyString())).thenThrow(NoSuchElementException.class);

        CEServiceResponse response = postService.getPostById("myrandompostid");
        assertTrue(response.isError());
    }

    @ParameterizedTest
    @ValueSource(ints = {3,10,50})
    void getAllChildrenSummaryTest(int count) {
        Channel channel = Mockito.mock(Channel.class);
        Post parent = new Post();
        parent.setChannel(channel);

        List<Post> children = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Post mockChild = new Post();
            mockChild.setChannel(channel);
            children.add(mockChild);

        }

        Mockito.when(postRepository.findById(Mockito.anyString())).thenReturn(Optional.of(parent));
        Mockito.when(postRepository.findAllByParentEquals(parent)).thenReturn(Optional.of(children));

        CEServiceResponse response = postService.getAllChildrenSummary("myrandompostid");
        assertTrue(response.isSuccess());
        assertEquals(((List<?>) response.getData()).size(), children.size());
    }

    @Test
    void getAllChildrenSummaryErrorTest() {
        Mockito.when(postRepository.findById(Mockito.anyString())).thenThrow(NoSuchElementException.class);

        CEServiceResponse response1 = postService.getAllChildrenSummary("myrandompostid");
        assertTrue(response1.isError());
    }

    @Test
    void getAllChildrenSummaryError2Test() {
        Mockito.when(postRepository.findById(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(Post.class)));
        Mockito.when(postRepository.findAllByParentEquals(Mockito.any(Post.class))).thenThrow(NoSuchElementException.class);

        CEServiceResponse response2 = postService.getAllChildrenSummary("myrandompostid");
        assertTrue(response2.isError());
    }

    @Test
    void savePostTest() {
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(Mockito.mock(User.class));
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(Mockito.mock(Post.class));

        CEServiceResponse response = postService.savePost(Mockito.mock(Post.class));
        assertTrue(response.isSuccess());
        assertInstanceOf(Post.class, response.getData());
    }

    @Test
    void savePostErrorTest() {
        User author = new User();
        author.setId(new ObjectId().toHexString());

        Post post = new Post();
        post.setAuthor(author);

        Mockito.when(withAuthentication().getPrincipal()).thenReturn(author);
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenThrow(DuplicateKeyException.class);

        CEServiceResponse response1 = postService.savePost(post);
        assertTrue(response1.isError());
        assertEquals(response1.getMessage(), String.format(CEUserErrorMessage.ENTITY_ALREADY_EXISTS, "post"));

        User randomUser = new User();
        randomUser.setId(new ObjectId().toHexString());

        Mockito.when(withAuthentication().getPrincipal()).thenReturn(randomUser);

        CEServiceResponse response2 = postService.savePost(post);
        assertTrue(response2.isError());
        assertEquals(response2.getMessage(), String.format(CEUserErrorMessage.ENTITY_NOT_BELONG_TO_USER, "post"));
    }

    @Test
    void saveReactionTest() {
        User author = new User();
        author.setId(new ObjectId().toHexString());

        Post post = new Post();
        post.setAuthor(author);
        post.setId(new ObjectId().toHexString());

        Reaction reaction = new Reaction();
        reaction.setPost(post);

        Mockito.when(withAuthentication().getPrincipal()).thenReturn(author);
        Mockito.when(postRepository.findById(Mockito.anyString())).thenReturn(Optional.of(post));
        Mockito.when(mongoTemplate.save(Mockito.any(Reaction.class), Mockito.anyString())).thenReturn(reaction);

        CEServiceResponse response = postService.saveReaction(reaction);
        assertTrue(response.isSuccess());
        assertInstanceOf(Reaction.class, response.getData());
    }

    @Test
    void saveReactionErrorTest() {
        Mockito.when(withAuthentication().getPrincipal()).thenReturn(Mockito.mock(User.class));

        Reaction reaction = Mockito.mock(Reaction.class, Mockito.RETURNS_DEEP_STUBS);

        Mockito.when(postRepository.findById(Mockito.any())).thenThrow(NoSuchElementException.class);

        CEServiceResponse response1 = postService.saveReaction(reaction);
        assertTrue(response1.isError());
        assertEquals(response1.getMessage(), String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "post"));

        Mockito.reset(postRepository);

        Reaction reaction2 = new Reaction();
        Post post = new Post();
        post.setId(new ObjectId().toHexString());
        User user = new User();
        user.setId(new ObjectId().toHexString());
        post.setAuthor(user);
        reaction2.setPost(post);

        Mockito.when(postRepository.findById(Mockito.anyString())).thenReturn(Optional.of(post));
        CEServiceResponse response2 = postService.saveReaction(reaction2);

        assertTrue(response2.isError());
        assertEquals(response2.getMessage(), String.format(CEUserErrorMessage.ENTITY_NOT_BELONG_TO_USER, "post"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0,10})
    void summarisedPostsTest(int count) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Post post = new Post();
            post.setChannel(Mockito.mock(Channel.class));
            posts.add(post);
        }

        Method summarisedPostsMethod = postService.returnSummarisedPostsWithAccess();
        Object result = summarisedPostsMethod.invoke(postService, posts);
        assertInstanceOf(List.class, result);
    }

    @Test
    void summarisedPostsErrorTest() throws NoSuchMethodException {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            // not adding a channel here to posts so checking NullPointerException
            posts.add(Mockito.mock(Post.class));
        }

        Method summarisedPostsMethod = postService.returnSummarisedPostsWithAccess();
        Exception e = assertThrows(InvocationTargetException.class, () -> summarisedPostsMethod.invoke(postService, posts));
        assertInstanceOf(NullPointerException.class, e.getCause());
    }
}
