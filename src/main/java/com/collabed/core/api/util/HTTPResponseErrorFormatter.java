package com.collabed.core.api.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Stream;

@Log4j2
public class HTTPResponseErrorFormatter {
    public static List<String> format(Errors errors) {
        Stream<String> errorStream = errors.getAllErrors().stream().map(
                o -> ((FieldError) o).getField() + ": " + o.getDefaultMessage()
        );
        List<String> errorList = errorStream.toList();
        log.info(String.format("Request body constraint validation failed: %s", errorList));
        return errorList;
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
