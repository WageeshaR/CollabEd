package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.repository.channel.ForumRepository;
import com.collabed.core.service.util.CEServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ForumServiceTests {
    private ForumRepository forumRepository;
    private ForumService forumService;

    @BeforeEach
    public void setup() {
        forumRepository = Mockito.mock(ForumRepository.class);
        forumService = new ForumService(forumRepository);
    }

    @Test
    public void saveForumTest() {
        Forum forum = new Forum();
        Mockito.when(forumRepository.save(Mockito.any(Forum.class))).thenReturn(forum);

        CEServiceResponse response = forumService.createForum(forum);
        assertTrue(response.isSuccess());
    }

    @Test
    public void saveForumErrorTest() {
        Forum forum = new Forum();
        Mockito.when(forumRepository.save(Mockito.any(Forum.class))).thenThrow(RuntimeException.class);

        CEServiceResponse response = forumService.createForum(forum);
        assertTrue(response.isError());
    }
}
