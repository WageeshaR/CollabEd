package com.collabed.core.service;

import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.location.Address;
import com.collabed.core.data.repository.AddressRepository;
import com.collabed.core.data.repository.InstitutionRepository;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Mockito.when(addressRepository.insert(Mockito.any(Address.class))).thenReturn(address);
        Mockito.when(institutionRepository.insert(Mockito.any(Institution.class))).thenReturn(institution);

        Institution result = institutionService.save(institution);
        assertEquals(result.getName(), "testinstitution");
        assertEquals(result.getAddress().getId(), address.getId());
    }

    @Test
    public void saveNoAddressTest() {
        Institution institution = Mockito.mock(Institution.class);
        CEWebRequestError error = assertThrows(CEWebRequestError.class, () -> institutionService.save(institution));
        assertEquals(error.getMessage(), CEUserErrorMessage.INSTITUTION_ADDRESS_NOT_NULL);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,20})
    public void allTest(int num) {
        List<Institution> institutions = new ArrayList<>();
        for (int i=0; i<num; i++)
            institutions.add(Mockito.mock(Institution.class));
        Mockito.when(institutionRepository.findAll()).thenReturn(institutions);

        List<Institution> result = institutionService.getAll();
        assertEquals(result.size(), num);
    }
}
