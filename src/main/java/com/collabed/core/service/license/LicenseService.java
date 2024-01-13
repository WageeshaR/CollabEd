package com.collabed.core.service.license;

import com.collabed.core.data.model.Session;
import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.license.LicenseOption;
import com.collabed.core.data.model.license.LicenseSession;
import com.collabed.core.data.repository.LicenseRepository;
import com.collabed.core.data.repository.SessionRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEServiceError;
import com.collabed.core.service.util.CEServiceResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final SessionRepository sessionRepository;

    public CEServiceResponse getAllOptions() {
        List<LicenseOption> options = new ArrayList<>();
        try {
            List<LicenseModel> models = licenseRepository.findAll();

            for (int i=0; i<models.size(); i++) {
                LicenseOption option = new LicenseOption();
                option.setId(Integer.toString(i));
                option.setModel(models.get(i));
                options.add(option);
            }
        } catch (RuntimeException e) {
            log.error(String.format(
                    CEInternalErrorMessage.SERVICE_QUERY_FAILED, "license"
            ));
            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "license")).data(e);
        }

        return CEServiceResponse.success().data(options);
    }

    public CEServiceResponse initSession(LicenseOption option, String sessionKey) {
        LicenseSession session = new LicenseSession();
        session.setLicenseModel(option.getModel());
        session.setSessionKey(sessionKey);

        try {
            sessionRepository.save(session);
            session.setSessionKey(null);

            return CEServiceResponse.success().data(session);
        } catch (Exception e) {
            log.error(
                    String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "license")
            );

            return CEServiceResponse.error(String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "license")).data(e);
        }
    }
}
