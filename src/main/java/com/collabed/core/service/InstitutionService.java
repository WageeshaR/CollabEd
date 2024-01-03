package com.collabed.core.service;

import com.collabed.core.data.model.institution.Institution;
import com.collabed.core.data.model.location.Address;
import com.collabed.core.data.repository.AddressRepository;
import com.collabed.core.data.repository.InstitutionRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEUserErrorMessage;
import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.service.util.CEServiceResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final AddressRepository addressRepository;

    public CEServiceResponse save(Institution institution) {
        try {
            if (institution.getAddress() == null) {
                throw new CEWebRequestError(
                        String.format(CEUserErrorMessage.ENTITY_PROPERTY_MUST_NOT_BE_NULL, "Institution", "address")
                );
            }
            Address address = addressRepository.save(institution.getAddress());
            institution.setAddress(address);
            Institution savedInstitution = institutionRepository.save(institution);
            log.info("Institution saved successfully");
            return CEServiceResponse.success().data(savedInstitution);
        } catch (RuntimeException e){
            log.error("Error saving institution: " + e);
            return CEServiceResponse.error().data(e);
        }
    }

    public CEServiceResponse getAll() {
        try {
            List<Institution> institutions = institutionRepository.findAll();
            return CEServiceResponse.success().data(institutions);
        } catch (RuntimeException e) {
            log.error(String.format(CEInternalErrorMessage.SERVICE_QUERY_FAILED, "institution"));
            return CEServiceResponse.error().data(e);
        }
    }
}
