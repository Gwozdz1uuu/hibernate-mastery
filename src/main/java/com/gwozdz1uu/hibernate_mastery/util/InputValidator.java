package com.gwozdz1uu.hibernate_mastery.util;

import com.gwozdz1uu.hibernate_mastery.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class InputValidator {

    public void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    public void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    public void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }
}
