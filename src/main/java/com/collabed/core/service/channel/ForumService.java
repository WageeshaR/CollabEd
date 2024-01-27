package com.collabed.core.service.channel;

import com.collabed.core.data.model.channel.Forum;
import com.collabed.core.data.repository.channel.ForumRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.service.util.CEServiceResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
