package com.collabed.core;

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

    public static Matcher<? super String> sessionIdLenMatcher(int len) {
        return new Matcher<String>() {
            @Override
            public void describeTo(Description description) {}
            @Override
            public boolean matches(Object o) {
                return o instanceof String && ((String) o).length() == len;
            }
            @Override
            public void describeMismatch(Object o, Description description) {}
            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
        };
    }
}
