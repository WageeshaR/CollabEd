package com.collabed.core.api.util;

import com.collabed.core.util.RandomUtil;

public class LicenseUtil {
    private static final int SESSION_KEY_LEN = 21;
    public static String generateSessionKey() {
        return new RandomUtil.RandomString(SESSION_KEY_LEN).nextString();
    }
}
