package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
public class JdbcValidator {
    private final Validator validator;

    public JdbcValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> errorSet = validator.validate(object);
        if (errorSet.isEmpty()) {
            return;
        }
        throw new ConstraintViolationException(errorSet);
    }
}
