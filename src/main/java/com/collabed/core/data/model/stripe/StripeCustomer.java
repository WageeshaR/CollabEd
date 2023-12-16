package com.collabed.core.data.model.stripe;

import com.collabed.core.data.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document
public class StripeCustomer {
    @Id
    private String id;
    @NotNull
    @DocumentReference
    private User user;
    @NotNull
    @JsonProperty("stripe_customer_id")
    private String stripeCustomerId;
}
