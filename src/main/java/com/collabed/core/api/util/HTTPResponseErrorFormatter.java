package com.collabed.core.api.util;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import java.util.stream.Stream;

public class HTTPResponseErrorFormatter {
    public static Stream<String> format(Errors errors) {
        return errors.getAllErrors().stream().map(
                o -> ((FieldError) o).getField() + ": " + o.getDefaultMessage()
        );
    }

    public static String camelToSnake(String str)
    {
        StringBuilder result = new StringBuilder();

        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            }

            else {
                result.append(ch);
            }
        }

        // return the result
        return result.toString();
    }
}
