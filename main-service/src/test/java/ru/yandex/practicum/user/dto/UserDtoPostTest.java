package ru.yandex.practicum.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoPostTest {
    @Test
    void validDto() {
        UserDtoPost dto = new UserDtoPost("Иван", "ivan@example.com");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDtoPost>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void nameBlank() {
        UserDtoPost dto = new UserDtoPost("", "email@example.com");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDtoPost>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void nameTooShort() {
        UserDtoPost dto = new UserDtoPost("A", "email@example.com");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDtoPost>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }
}