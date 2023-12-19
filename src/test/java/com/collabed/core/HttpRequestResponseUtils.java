package com.collabed.core;

import com.collabed.core.api.util.LicenseUtil;
import com.collabed.core.data.model.license.LicenseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.util.List;

public class HttpRequestResponseUtils {
    public static String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    public static  <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    public static Matcher<?> countMatcher(int numUsers) {
        return new Matcher<List<?>>() {
            @Override
            public void describeTo(Description description) {}
            @Override
            public boolean matches(Object o) {
                return o instanceof List<?> && ((List<?>) o).size() == numUsers;
            }
            @Override
            public void describeMismatch(Object o, Description description) {}
            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
        };
    }

    public static Matcher<? super String> sessionKeyValidator(int len) {
        return new Matcher<>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matches(Object o) {
                return LicenseUtil.isValid((String) o);
            }

            @Override
            public void describeMismatch(Object o, Description description) {
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
            }
        };
    }

    public static Matcher<? super List<LicenseType>> licenseTypesMatcher() {
        return new Matcher<>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof List<?>
                        && ((List<?>) o).size() == 3
                        && ((List<?>) o).contains(LicenseType.INDIVIDUAL.type())
                        && ((List<?>) o).contains(LicenseType.GROUP.type())
                        && ((List<?>) o).contains(LicenseType.INSTITUTIONAL.type());
            }

            @Override
            public void describeMismatch(Object o, Description description) {
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
            }
        };
    }
}