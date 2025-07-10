package ru.yandex.practicum.category.dto;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryDtoTest {

    @Test
    void testNoArgsConstructor() {
        CategoryDto dto = new CategoryDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        CategoryDto dto = new CategoryDto(1L, "TestCategory");
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("TestCategory");
    }

    @Test
    void testSettersAndGetters() {
        CategoryDto dto = new CategoryDto();
        dto.setId(2L);
        dto.setName("Java");

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Java");
    }
}