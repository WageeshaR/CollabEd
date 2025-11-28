package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.model.channel.Thread;
import com.collabed.core.data.repository.channel.ThreadRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Service
@Log4j2
public class ForumService {
    private final MongoTemplate mongoTemplate;
    private final ThreadRepository threadRepository;

    @Autowired
    ForumService(MongoTemplate mongoTemplate, ThreadRepository threadRepository) {
        this.mongoTemplate = mongoTemplate;
        this.threadRepository = threadRepository;
    }

    public CEServiceResponse createForum(Forum forum) {
        try {
            Forum savedForum = mongoTemplate.save(forum);
            log.info("Forum saved successfully");

            return CEServiceResponse.success().data(savedForum);

        } catch (RuntimeException e) {
            log.error(String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum"));

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum")
            ).build();
        }
    }

    public CEServiceResponse createThread(Thread thread) {
        try {
            // TODO: implementation for checks on chained threads
            var savedThread = threadRepository.save(thread);
            log.info("Thread saved successfully");

            return CEServiceResponse.success().data(savedThread);

        } catch (RuntimeException e) {
            log.error(
                    String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum")
            );

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum")
            ).data(e);
        }
    }

    public CEServiceResponse resolveThread(String threadId) {
        try {
            var thread = threadRepository.findById(threadId).orElseThrow();

            thread.setResolved(true);
            var resolvedThread = threadRepository.save(thread);

            log.info("Thread resolved successfully");
            return CEServiceResponse.success().data(resolvedThread);

        } catch (NoSuchElementException e) {
            log.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "thread"));

            return CEServiceResponse.error(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "thread")
            ).build();

        } catch (RuntimeException e) {
            log.error(String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum"));

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum")
            ).build();
        }
    }

    public CEServiceResponse addUserToThread(String threadId, String userId) {
        return CEServiceResponse.success().build();
    }
}
