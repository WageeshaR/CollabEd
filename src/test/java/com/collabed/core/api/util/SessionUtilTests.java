package com.collabed.core.api.util;

import com.collabed.core.util.SessionUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionUtilTests {
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "dsf7243jbhb9i238y4"})
    void generateSessionKeyTest(String param) {
        assertEquals(SessionUtil.generateSessionKey(param).getClass().getName(), String.class.getName());
    }
}
