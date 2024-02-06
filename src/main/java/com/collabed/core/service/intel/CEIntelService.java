package com.collabed.core.service.intel;

import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.internal.SimpleIntelGateway;
import com.collabed.core.service.intel.criteria.Criteria;
import com.collabed.core.service.intel.criteria.CriteriaTarget;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Service
@Log4j2
public class CEIntelService {
    @Qualifier("requestScopedIntelGateway")
    private final SimpleIntelGateway intelGateway;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CEIntelService(MongoTemplate mongoTemplate, SimpleIntelGateway intelGateway) {
        this.mongoTemplate = mongoTemplate;
        this.intelGateway = intelGateway;
    }

    public boolean setupGateway() {
        boolean initDone = intelGateway.initialise();
        if (initDone) {
            log.info("Successfully initialised bean " + SimpleIntelGateway.class.getName() + " for thread " + Thread.currentThread().getId());
            return true;
        }
        log.error("Error initialising bean " + SimpleIntelGateway.class.getName() + " for thread " + Thread.currentThread().getId());
        return false;
    }

    public List<?> getCuratedListOfType(Class<?> type) {
        // init criteria builder
        var criteriaBuilder = Criteria.filter();

        try {
            var collectionName = mongoTemplate.getCollectionName(type);

            // set input
            var input = new CriteriaTarget(CriteriaTarget.TargetType.DB_FETCH);
            input.addTargets(collectionName);
            criteriaBuilder.input(input);

        } catch (MappingException | InvalidDataAccessApiUsageException e) {
            log.error(e);
            return List.of();
        }

        if (criteriaBuilder.hasInput()) {
            // set subject
            var subject = new CriteriaTarget(CriteriaTarget.TargetType.SUPPLIED);
            var userProfile = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getProfile();

            if (userProfile == null) {
                log.error("User profile not found. Operation stopped.");
                return List.of();
            }
            var primaryInterest = userProfile.getPrimaryInterest();
            subject.addTargets(primaryInterest);
            criteriaBuilder.subject(subject);
        }

        var intelCriteria = criteriaBuilder.build();
        try {
            intelGateway.config(intelCriteria);

            boolean fetched = intelGateway.fetchSync(List.class);

            if (fetched)
                return intelGateway.returnListResult();
        } catch (URISyntaxException e) {
            log.error("Invalid URI syntax provided: " + e);
        }
        return List.of();
    }
}
