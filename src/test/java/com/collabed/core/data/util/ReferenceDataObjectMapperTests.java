package com.collabed.core.data.util;

import com.collabed.core.data.model.Institution;
import com.collabed.core.data.repository.InstitutionRepository;
import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.runtime.exception.CEReferenceObjectMappingError;
import com.collabed.core.util.TypeResolverMap;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReferenceDataObjectMapperTests {
    @MockBean
    private InstitutionRepository institutionRepository;

    @Test
    public void readReferencedObject() {
        Institution institution = new Institution();
        institution.setId(new ObjectId().toHexString());
        ReferenceDataObjectMapper<Institution> mapper = new ReferenceDataObjectMapper<>();
        Mockito.when(institutionRepository.findById(Mockito.anyString())).thenReturn(Optional.of(institution));
        Object referencedObject = mapper.readReferenceObject(
                institution, TypeResolverMap.RepositoryTypeResolver.INSTITUTION_REPOSITORY);
        assertEquals(referencedObject.getClass().getName(), Institution.class.getName());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"RandomTypeName"})
    public void readReferencedObject(String param) {
        Institution institution = new Institution();
        ReferenceDataObjectMapper<Institution> mapper = new ReferenceDataObjectMapper<>();
        Exception e = assertThrows(CEReferenceObjectMappingError.class, () -> mapper.readReferenceObject(institution, param));
        assertEquals(e.getMessage(), CEInternalErrorMessage.MAPPING_FOR_GIVEN_NAME_UNAVAILABLE);

    }
}
