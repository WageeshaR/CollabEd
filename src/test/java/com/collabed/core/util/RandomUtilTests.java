package com.collabed.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.collabed.core.util.RandomUtil.RandomString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomUtilTests {

    @Test
    public void randomStringDefaultLengthTest() {
        assertEquals(new RandomString().nextString().length(), RandomString.DEFAULT_LENGTH);
    }

    @ParameterizedTest
    @ValueSource(ints = {10,50,100})
    public void randomStringWithLengthTest(int len) {
        assertEquals(new RandomString(len).nextString().length(), len);
    }
}
