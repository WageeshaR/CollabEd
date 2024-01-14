package com.collabed.core.service.intel;

import com.collabed.core.data.model.user.User;
import com.collabed.core.data.model.user.profile.Profile;
import com.collabed.core.internal.SimpleIntelGateway;
import com.collabed.core.service.intel.criteria.Criteria;
import com.collabed.core.service.intel.criteria.CriteriaTarget;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service
@Log4j2
public class CEIntelService {
    @Resource(name = "requestScopedIntelGateway")
    SimpleIntelGateway intelGateway;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CEIntelService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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
        Criteria.CriteriaBuilder criteriaBuilder = Criteria.filter();

        try {
            String collectionName = mongoTemplate.getCollectionName(type);
            // set input
            CriteriaTarget input = new CriteriaTarget(CriteriaTarget.TargetType.DB_FETCH);
            input.addTargets(collectionName);
            criteriaBuilder.input(input);
        } catch (MappingException | InvalidDataAccessApiUsageException e) {
            log.error(e);
            return List.of();
        }

        if (criteriaBuilder.hasInput()) {
            // set subject
            CriteriaTarget subject = new CriteriaTarget(CriteriaTarget.TargetType.SUPPLIED);
            Profile userProfile = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getProfile();
            String primaryInterest = userProfile.getPrimaryInterest();
            subject.addTargets(primaryInterest);
            criteriaBuilder.subject(subject);
        }

        Criteria intelCriteria = criteriaBuilder.build();
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
