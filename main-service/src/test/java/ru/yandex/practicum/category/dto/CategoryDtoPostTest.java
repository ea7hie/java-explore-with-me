package ru.yandex.practicum.category.dto;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryDtoPostTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validCategoryDtoPost() {
        CategoryDtoPost dto = new CategoryDtoPost("TestCategory");
        Set<ConstraintViolation<CategoryDtoPost>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankNameValidation() {
        CategoryDtoPost dto1 = new CategoryDtoPost("");
        CategoryDtoPost dto2 = new CategoryDtoPost("   ");

        assertValidation(dto1, 2);
        assertValidation(dto2, 1);
    }

    @Test
    void nullNameValidation() {
        CategoryDtoPost dto = new CategoryDtoPost(null);
        assertValidation(dto, 1);
    }

    @Test
    void shortNameValidation() {
        CategoryDtoPost dto = new CategoryDtoPost("Ta");
        assertValidation(dto, 1);
    }

    @Test
    void longNameValidation() {
        String longName = new String(new char[255]).replace('\0', 'a');
        CategoryDtoPost dto = new CategoryDtoPost(longName);
        assertValidation(dto, 1);
    }

    private void assertValidation(CategoryDtoPost dto, int expectedViolations) {
        Set<ConstraintViolation<CategoryDtoPost>> violations = validator.validate(dto);
        assertThat(violations).hasSize(expectedViolations);
    }

    @Test
    void setterGetterBehavior() {
        CategoryDtoPost dto = new CategoryDtoPost();
        dto.setName("Java");
        assertThat(dto.getName()).isEqualTo("Java");
    }
}