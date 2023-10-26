package org.apostolis.service;

import jakarta.validation.*;
import java.util.Set;

public class ValidationUtils {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> void validateInput(T t){
        Set<ConstraintViolation<T>> constraintViolations =
                validator.validate(t);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
