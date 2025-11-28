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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ForumServiceTests {
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private ThreadRepository threadRepository;
    @InjectMocks
    private ForumService forumService;

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
    public void createThreadTest() {
        Thread thread = new Thread();
        thread.setId(new ObjectId().toHexString());

        Mockito.when(threadRepository.save(Mockito.any(Thread.class))).thenReturn(thread);

        CEServiceResponse response = forumService.createThread(thread);

        assertTrue(response.isSuccess());
        assertInstanceOf(Thread.class, response.getData());
        assertEquals(((Thread) response.getData()).getId(), thread.getId());
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
