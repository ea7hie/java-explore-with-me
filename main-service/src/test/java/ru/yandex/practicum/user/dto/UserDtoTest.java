package ru.yandex.practicum.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoTest {
    private UserDto userDtoUnderTest;

    @BeforeEach
    void beforeEach() {
        userDtoUnderTest = new UserDto(1L, "Ivan Petrov", "ivan@example.com");
    }

    @Test
    void constructorWithParams_correctlyInitializesFields() {
        UserDto dto = new UserDto(42L, "John Doe", "john@domain.com");
        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@domain.com");
    }

    @Test
    void constructorNoArgsConstructor_initializesFieldsToNull() {
        UserDto emptyDto = new UserDto();
        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getName()).isNull();
        assertThat(emptyDto.getEmail()).isNull();
    }

    @Test
    void setters_correctlyAssignValues() {
        userDtoUnderTest.setId(22L);
        userDtoUnderTest.setName("Maria Smith");
        userDtoUnderTest.setEmail("maria@example.com");

        assertThat(userDtoUnderTest.getId()).isEqualTo(22L);
        assertThat(userDtoUnderTest.getName()).isEqualTo("Maria Smith");
        assertThat(userDtoUnderTest.getEmail()).isEqualTo("maria@example.com");
    }
}