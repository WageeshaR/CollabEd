package com.collabed.core.api.util;

import com.collabed.core.runtime.exception.CEWebRequestError;
import com.collabed.core.util.RandomUtil;
import org.apache.tomcat.util.codec.binary.Base64;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SessionUtil {
    private static final int SESSION_KEY_LEN = 14;
    private static final Base64 base64 = new Base64();
    public static String generateSessionKey(String currentKey) {
        if (currentKey != null && isValid(currentKey))
            return currentKey;
        return new RandomUtil.RandomString(SESSION_KEY_LEN).nextString() + b64StringFromDate();
    }

    private static String b64StringFromDate() {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.DATE, 3);
        String expiryAsString = df.format(expiry.getTime());
        return new String(base64.encode(expiryAsString.getBytes()));
    }

    private static Date dateFromB64String(String dateString) {
        String decoded = new String(base64.decode(dateString.getBytes()));
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(decoded);
        } catch (ParseException e) {
            throw new CEWebRequestError("Invalid session key.");
        }
    }

    public static boolean isValid(String key) {
        try {
            String b64Date = key.substring(SESSION_KEY_LEN);
            Date expiry = dateFromB64String(b64Date);
            return expiry.after(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return false;
        }
    }
}
