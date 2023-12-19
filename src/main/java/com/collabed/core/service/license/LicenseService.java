package com.collabed.core.service.license;

import com.collabed.core.data.model.Session;
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
            LicenseOption option = new LicenseOption();
            option.setId(Integer.toString(i));
            option.setModel(models.get(i));
            options.add(option);
        }
        return options;
    }

    public Session initSession(LicenseOption option, String sessionKey) {
        LicenseSession session = new LicenseSession();
        session.setLicenseModel(option.getModel());
        session.setSessionKey(sessionKey);
        try {
            sessionRepository.save(session);
            session.setSessionKey(null);
            return session;
        } catch (Exception e) {
            throw new CEServiceError(e.getMessage());
        }
    }
}
