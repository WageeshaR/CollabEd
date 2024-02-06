package com.collabed.core.data.model.stripe;

import com.collabed.core.data.model.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Data
@Document
public class StripeCustomer {
    @Id
    private String id;
    @NotNull
    @DocumentReference
    private User user;
    @NotNull
    private String stripeCustomerId;
}
