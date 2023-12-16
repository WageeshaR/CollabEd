package com.collabed.core.service.license;

import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.license.LicenseOption;
import com.collabed.core.data.model.license.LicenseSession;
import com.collabed.core.data.repository.LicenseRepository;
import com.collabed.core.data.repository.SessionRepository;
import com.collabed.core.runtime.exception.CEServiceError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final SessionRepository sessionRepository;

    public List<LicenseOption> getAllOptions() {
        List<LicenseOption> options = new ArrayList<>();
        List<LicenseModel> models = licenseRepository.findAll();
        for (int i=0; i<models.size(); i++) {
            LicenseOption option = new LicenseOption(Integer.toString(i), models.get(i));
            options.add(option);
        }
        return options;
    }

    public boolean initSession(LicenseOption option) {
        LicenseSession session = new LicenseSession();
        session.setLicenseModel(option.getModel());
        try {
            sessionRepository.save(session);
            return true;
        } catch (Exception e) {
            throw new CEServiceError(e.getMessage());
        }
    }
}
