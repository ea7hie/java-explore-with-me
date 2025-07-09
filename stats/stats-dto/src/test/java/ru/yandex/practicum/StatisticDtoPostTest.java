package ru.yandex.practicum;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticDtoPostTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidDto() {
        StatisticDtoPost dto = new StatisticDtoPost();
        dto.setApp("myApp");
        dto.setUri("/home");
        dto.setIp("192.168.0.1");
        dto.setTimestamp(LocalDateTime.now());

        Set<ConstraintViolation<StatisticDtoPost>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    public void testNullFields() {
        StatisticDtoPost dto = new StatisticDtoPost();

        Set<ConstraintViolation<StatisticDtoPost>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "All fields should be invalid when null or empty");
    }

    @Test
    public void testInvalidIpLength() {
        StatisticDtoPost dto = new StatisticDtoPost();
        dto.setApp("myApp");
        dto.setUri("/test");
        dto.setIp("123"); // слишком короткий IP
        dto.setTimestamp(LocalDateTime.now());

        Set<ConstraintViolation<StatisticDtoPost>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ip")));
    }

    @Test
    public void testEmptyStrings() {
        StatisticDtoPost dto = new StatisticDtoPost();
        dto.setApp("");
        dto.setUri("");
        dto.setIp("");
        dto.setTimestamp(null);

        Set<ConstraintViolation<StatisticDtoPost>> violations = validator.validate(dto);
        assertEquals(5, violations.size(), "app, uri и ip должны быть не пустыми, " +
                "ip должен быть соответствующий длины, timestamp не должны быть null");
    }

    @Test
    public void testMissingTimestamp() {
        StatisticDtoPost dto = new StatisticDtoPost();
        dto.setApp("app");
        dto.setUri("/uri");
        dto.setIp("127.0.0.1");

        Set<ConstraintViolation<StatisticDtoPost>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("timestamp", violations.iterator().next().getPropertyPath().toString());
    }
}