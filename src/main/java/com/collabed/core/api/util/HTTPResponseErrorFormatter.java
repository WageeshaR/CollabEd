package com.collabed.core.api.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Log4j2
public final class HTTPResponseErrorFormatter {
    public static List<String> format(Errors errors) {
        Stream<String> errorStream = errors.getAllErrors().stream().filter(fieldErrors).map(formatMessage);
        List<String> errorList = errorStream.toList();
        log.info(String.format("Request body constraint validation failed: %s", errorList));
        return errorList;
    }

    /**
     * Returns a Predicate<> to filter only FieldErrors
     */
    static final Predicate<ObjectError> fieldErrors = o -> o instanceof FieldError;

    /**
     * Returns a Function<> to format error message
     */
    static final Function<ObjectError, String> formatMessage = o -> ((FieldError) o).getField() + ": " + o.getDefaultMessage();

    /**
     * Simple function to convert camel case String to snake case
     * @param str: String to convert
     * @return converted String
     */
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
            else result.append(ch);
        }
        return result.toString();
    }
}
