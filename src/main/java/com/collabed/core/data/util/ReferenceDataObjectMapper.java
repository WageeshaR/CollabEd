package com.collabed.core.data.util;

import com.collabed.core.runtime.exception.CEInternalErrorMessage;
import com.collabed.core.util.TypeResolverMap;
import com.collabed.core.runtime.exception.CEReferenceObjectMappingError;
import com.collabed.core.util.SpringContext;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReferenceDataObjectMapper<T> {
    private MongoRepository<T, String> repository;

    public T readReferenceObject(T object, String repositoryName) {
        if (TypeResolverMap.RepositoryTypeResolver.classNameMap.keySet().stream().noneMatch(k -> Objects.equals(k, repositoryName)))
            throw new CEReferenceObjectMappingError(CEInternalErrorMessage.MAPPING_FOR_GIVEN_NAME_UNAVAILABLE);

        try {
            repository = (MongoRepository<T, String>) SpringContext.getBean(TypeResolverMap.RepositoryTypeResolver.classNameMap.get(repositoryName));
        } catch (ClassCastException e) {
            throw new CEReferenceObjectMappingError("Error resolving repository bean from classname " + repositoryName);
        }

        Class<?> objectClass = object.getClass();
        try {
            Method getId = objectClass.getMethod("getId");
            String objectId = (String) getId.invoke(object);
            return repository.findById(objectId).orElseThrow();
        } catch (NoSuchMethodException e) {
            throw new CEReferenceObjectMappingError("getId method of object type " + objectClass.getName() + " is not found");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new CEReferenceObjectMappingError("Failed to invoke getId of object type " + objectClass.getName());
        }
    }
}
