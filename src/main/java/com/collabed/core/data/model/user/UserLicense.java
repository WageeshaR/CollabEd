package com.collabed.core.data.model.user;

import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
public class UserLicense {
    @NotNull
    private LicenseModel licenseModel;
    @NotNull
    @DocumentReference
    private User licensedUnder;
}
