package ru.yandex.practicum.category.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;
import ru.yandex.practicum.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category toCategory(CategoryDtoPost categoryDtoPost) {
        return new Category(-1L, categoryDtoPost.getName());
    }
}
