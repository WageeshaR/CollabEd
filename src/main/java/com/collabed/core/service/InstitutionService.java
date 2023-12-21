package com.collabed.core.service;

import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.location.Address;
import com.collabed.core.data.repository.AddressRepository;
import com.collabed.core.data.repository.InstitutionRepository;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InstitutionService {
    private InstitutionRepository institutionRepository;
    private AddressRepository addressRepository;

    public Institution save(Institution institution) {
        if (institution.getAddress() == null) {
            throw new CEWebRequestError(CEUserErrorMessage.INSTITUTION_ADDRESS_NOT_NULL);
        }
        Address address = addressRepository.insert(institution.getAddress());
        institution.setAddress(address);
        return institutionRepository.insert(institution);
    }

    public List<Institution> getAll() {
        return institutionRepository.findAll();
    }
}
