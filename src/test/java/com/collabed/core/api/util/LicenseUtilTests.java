package com.collabed.core.api.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LicenseUtilTests {
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "dsf7243jbhb9i238y4"})
    public void generateSessionKeyTest(String param) {
        assertEquals(LicenseUtil.generateSessionKey(param).getClass().getName(), String.class.getName());
    }
}
