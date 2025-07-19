package ru.yandex.practicum.category.model;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validCategory() {
        Category category = new Category(1L, "TestCategory");
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidNullOrEmptyName() {
        Category emptyName = new Category(2L, "");
        Category nullName = new Category(3L, null);

        assertViolations(emptyName, 1);
        assertViolations(nullName, 1);
    }

    @Test
    void nameTooLong() {
        String longName = "a".repeat(51);
        Category category = new Category(4L, longName);
        assertViolations(category, 1);
    }

    @Test
    void validMinLength() {
        Category category = new Category(5L, "A");
        assertThat(validator.validate(category)).isEmpty();
    }

    @Test
    void settersAndGetters() {
        Category category = new Category();
        category.setId(6L);
        category.setName("Java");

        assertThat(category.getId()).isEqualTo(6L);
        assertThat(category.getName()).isEqualTo("Java");
    }

    private void assertViolations(Category category, int expected) {
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertThat(violations).hasSize(expected);
    }
}