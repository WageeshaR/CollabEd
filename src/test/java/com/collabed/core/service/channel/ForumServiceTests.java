package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.model.channel.Thread;
import com.collabed.core.data.repository.channel.ThreadRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ForumServiceTests {
    private MongoTemplate mongoTemplate;
    private ThreadRepository threadRepository;
    private ForumService forumService;

    @BeforeEach
    public void setup() {
        mongoTemplate = Mockito.mock(MongoTemplate.class);
        threadRepository = Mockito.mock(ThreadRepository.class);
        forumService = new ForumService(mongoTemplate, threadRepository);
    }

    @Test
    public void saveForumTest() {
        Forum forum = new Forum();
        Mockito.when(mongoTemplate.save(Mockito.any(Forum.class))).thenReturn(forum);

        CEServiceResponse response = forumService.createForum(forum);
        assertTrue(response.isSuccess());
    }

    @Test
    public void saveForumErrorTest() {
        Forum forum = new Forum();
        Mockito.when(mongoTemplate.save(Mockito.any(Forum.class))).thenThrow(RuntimeException.class);

        CEServiceResponse response = forumService.createForum(forum);
        assertTrue(response.isError());
    }

    @Test
    public void resolveThreadTest() {
        String threadId = new ObjectId().toHexString();

        Thread resolveThread = new Thread();
        resolveThread.setResolved(true);

        Mockito.when(threadRepository.findById(threadId)).thenReturn(Optional.of(Mockito.mock(Thread.class)));
        Mockito.when(threadRepository.save(Mockito.any(Thread.class))).thenReturn(resolveThread);

        CEServiceResponse response = forumService.resolveThread(threadId);

        assertTrue(response.isSuccess());
        assertInstanceOf(Thread.class, response.getData());

        assertTrue(((Thread) response.getData()).isResolved());
    }

    @Test
    public void resolveNoSuchElementErrorTest() {

        String threadId = new ObjectId().toHexString();

        CEServiceResponse response = forumService.resolveThread(threadId);

        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "thread"));
    }

    @Test
    public void resolveInternalErrorTest() {
        String threadId = new ObjectId().toHexString();

        Mockito.when(threadRepository.findById(threadId)).thenReturn(Optional.of(Mockito.mock(Thread.class)));

        Mockito.when(threadRepository.save(Mockito.any(Thread.class))).thenThrow(RuntimeException.class);

        CEServiceResponse response = forumService.resolveThread(threadId);

        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum"));

    }
}
