package ru.yandex.practicum.category.dto.mapper;


import org.junit.jupiter.api.Test;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;
import ru.yandex.practicum.category.model.Category;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    @Test
    void testToCategoryDto() {
        Category category = new Category(1L, "TestCategory");
        CategoryDto dto = CategoryMapper.toCategoryDto(category);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("TestCategory");
    }

    @Test
    void testToCategory() {
        CategoryDtoPost dtoPost = new CategoryDtoPost("NewCategory");
        Category category = CategoryMapper.toCategory(dtoPost);

        assertThat(category.getId()).isEqualTo(-1L);
        assertThat(category.getName()).isEqualTo("NewCategory");
    }
}