package com.collabed.core.util;

import com.collabed.core.data.repository.InstitutionRepository;

import java.util.HashMap;
import java.util.Map;

public class TypeResolverMap {
    public static class RepositoryTypeResolver {
        public static final String INSTITUTION_REPOSITORY = "InstitutionRepository";
        public static Map<String, Class<?>> classNameMap = new HashMap<>();
        static {
            classNameMap.put(INSTITUTION_REPOSITORY, InstitutionRepository.class);
        }
    }
}
