package com.collabed.core.service;

import com.collabed.core.data.model.Institution;
import com.collabed.core.data.model.location.Address;
import com.collabed.core.data.repository.AddressRepository;
import com.collabed.core.data.repository.InstitutionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InstitutionService {
    private InstitutionRepository institutionRepository;
    private AddressRepository addressRepository;

    public Institution save(Institution institution) {
        Address address = addressRepository.insert(institution.getAddress());
        institution.setAddressId(address.getId());
        return institutionRepository.insert(institution);
    }

    public List<Institution> getAll() {
        return institutionRepository.findAll();
    }
}
