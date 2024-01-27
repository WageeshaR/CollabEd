package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.repository.channel.ForumRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Log4j2
public class ForumService {
    private final ForumRepository forumRepository;

    @Autowired
    ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
    }

    public CEServiceResponse createForum(Forum forum) {
        try {
            Forum savedForum = forumRepository.save(forum);
            log.info("Forum saved successfully");

            return CEServiceResponse.success().data(savedForum);
        } catch (RuntimeException e) {
            log.error(String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum"));

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum")
            ).build();
        }
    }

    public CEServiceResponse resolve(String forumId) {
        try {
            Forum forum = forumRepository.findById(forumId).orElseThrow();

            forum.setResolved(true);
            forumRepository.save(forum);

            log.info("Forum resolved successfully");
            return CEServiceResponse.success().build();
        } catch (NoSuchElementException e) {
            log.error(String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "forum"));

            return CEServiceResponse.error(
                    String.format(CEUserErrorMessage.ENTITY_NOT_EXIST, "forum")
            ).build();
        } catch (RuntimeException e) {
            log.error(String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum"));

            return CEServiceResponse.error(
                    String.format(CEInternalErrorMessage.SERVICE_RUNTIME_ERROR, "forum")
            ).build();
        }
    }
}
