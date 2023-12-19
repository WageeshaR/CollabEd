package com.collabed.core.util;

import com.collabed.core.data.repository.InstitutionRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TypeResolver {
    public static class RepositoryTypeResolver {
        public static Map<String, Class<?>> classNameMap = new HashMap<>();
        static {
            classNameMap.put("InstitutionRepository", InstitutionRepository.class);
        }
    }
}
