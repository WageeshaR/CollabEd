package com.collabed.core.service;

import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.data.model.location.Address;
import com.collabed.core.data.repository.AddressRepository;
import com.collabed.core.data.repository.InstitutionRepository;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.util.CEServiceResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstitutionServiceTests {
    private InstitutionRepository institutionRepository;
    private AddressRepository addressRepository;
    private InstitutionService institutionService;

    @BeforeEach
    public void setup() {
        institutionRepository = Mockito.mock(InstitutionRepository.class);
        addressRepository = Mockito.mock(AddressRepository.class);
        institutionService = new InstitutionService(institutionRepository, addressRepository);
    }

    @Test
    public void saveTest() {
        Address address = new Address();
        address.setId(new ObjectId().toHexString());
        Institution institution = new Institution();
        institution.setName("testinstitution");
        institution.setAddress(address);
        Mockito.when(addressRepository.save(Mockito.any(Address.class))).thenReturn(address);
        Mockito.when(institutionRepository.save(Mockito.any(Institution.class))).thenReturn(institution);

        CEServiceResponse response = institutionService.save(institution);
        assertEquals(((Institution) response.getData()).getName(), "testinstitution");
        assertEquals(((Institution) response.getData()).getAddress().getId(), address.getId());
    }

    @Test
    public void saveNoAddressTest() {
        Institution institution = Mockito.mock(Institution.class);
        CEServiceResponse response = institutionService.save(institution);
        assertTrue(response.isError());
        assertEquals(
                ((CEWebRequestError) response.getData()).getMessage(),
                String.format(CEUserErrorMessage.ENTITY_PROPERTY_MUST_NOT_BE_NULL, "Institution", "address")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    public void allTest(int num) {
        List<Institution> institutions = new ArrayList<>();
        for (int i=0; i<num; i++)
            institutions.add(Mockito.mock(Institution.class));
        Mockito.when(institutionRepository.findAll()).thenReturn(institutions);

        CEServiceResponse response = institutionService.getAll();
        assertEquals(((List<?>) response.getData()).size(), num);
    }
}
