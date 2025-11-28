package com.collabed.core.data.repository.user;

import com.collabed.core.data.model.user.profile.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
}
