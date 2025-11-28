package com.collabed.core.util;

import com.collabed.core.runtime.exception.CEWebRequestError;
import org.apache.tomcat.util.codec.binary.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

public class SessionUtil {
    private static final int SESSION_KEY_LEN = 14;
    private static final Base64 base64 = new Base64();

    public static String generateSessionKey(String currentKey) {
        if (currentKey != null && isValid(currentKey))
            return currentKey;

        return new RandomUtil.RandomString(SESSION_KEY_LEN).nextString() + b64StringFromDate();
    }

    private static String b64StringFromDate() {
        var pattern = "MM/dd/yyyy HH:mm:ss";
        var dateFormat = new SimpleDateFormat(pattern);
        var expiry = Calendar.getInstance();

        expiry.add(Calendar.DATE, 3);
        var expiryAsString = dateFormat.format(expiry.getTime());

        return new String(base64.encode(expiryAsString.getBytes()));
    }

    private static Date dateFromB64String(String dateString) {
        var decoded = new String(base64.decode(dateString.getBytes()));
        var pattern = "MM/dd/yyyy HH:mm:ss";
        var dateFormat = new SimpleDateFormat(pattern);

        try {
            return dateFormat.parse(decoded);
        } catch (ParseException e) {
            throw new CEWebRequestError("Invalid session key.");
        }
    }

    public static boolean isValid(String key) {
        try {
            var b64Date = key.substring(SESSION_KEY_LEN);
            var expiry = dateFromB64String(b64Date);

            return expiry.after(Calendar.getInstance().getTime());

        } catch (Exception e) {
            return false;
        }
    }
}
