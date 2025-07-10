package ru.yandex.practicum.category.service;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(Long id);

    CategoryDto add(CategoryDtoPost category);

    CategoryDto update(Long id, CategoryDtoPost category);

    void delete(Long id);
}
