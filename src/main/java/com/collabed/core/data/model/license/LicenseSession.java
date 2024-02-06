package com.collabed.core.data.model.license;

import com.collabed.core.data.model.Session;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class LicenseSession extends Session {
    @NotNull
    @DocumentReference(lazy = true)
    private LicenseModel licenseModel;
    private boolean initStripe = false;
    private boolean addedPaymentMethod = false;
    private boolean addedAddress = false;
    private boolean licenseAgreement = false;
}
