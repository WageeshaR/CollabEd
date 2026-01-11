package com.collabed.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.collabed.core.data.model.Session;
import com.collabed.core.data.model.license.LicenseModel;
import com.collabed.core.data.model.license.LicenseOption;
import com.collabed.core.data.repository.LicenseRepository;
import com.collabed.core.data.repository.SessionRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.service.license.LicenseService;
import com.collabed.core.service.util.CEServiceResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTests {
    @Mock
    private LicenseRepository licenseRepository;
    @Mock
    private SessionRepository sessionRepository;
    @InjectMocks
    private LicenseService licenseService;

    LicenseServiceTests() {
        licenseRepository = Mockito.mock(LicenseRepository.class);
        sessionRepository = Mockito.mock(SessionRepository.class);
    }

    @BeforeEach
    void setup() {
        licenseService = new LicenseService(licenseRepository, sessionRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {3,10})
    void getAllOptionsTest(int count) {
        List<LicenseModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            models.add(Mockito.mock(LicenseModel.class));
        }
        Mockito.when(licenseRepository.findAll()).thenReturn(models);

        CEServiceResponse response = licenseService.getAllOptions();
        assertTrue(response.isSuccess());
        assertEquals(((List<?>)response.getData()).size(), models.size());
    }

    @Test
    void getAllOptionsErrorTest() {
        Mockito.when(licenseRepository.findAll()).thenThrow(RuntimeException.class);
        CEServiceResponse response = licenseService.getAllOptions();
        assertTrue(response.isError());
    }

    @Test
    void initSessionTest() {
        LicenseOption option = Mockito.mock(LicenseOption.class);
        Mockito.when(option.getModel()).thenReturn(Mockito.mock(LicenseModel.class));
        Mockito.when(sessionRepository.save(Mockito.any(Session.class))).thenReturn(null);

        CEServiceResponse response = licenseService.initSession(option, "myrandomsessionkey");
        assertTrue(response.isSuccess());
        assertInstanceOf(Session.class, response.getData());
    }

    @Test
    void initSessionErrorTest() {
        Mockito.when(sessionRepository.save(Mockito.any(Session.class))).thenThrow(RuntimeException.class);

        CEServiceResponse response = licenseService.initSession(Mockito.mock(LicenseOption.class), "myrandomsessionkey");
        assertTrue(response.isError());
        assertEquals(response.getMessage(), String.format(CEInternalErrorMessage.SERVICE_UPDATE_FAILED, "license"));
    }
}
