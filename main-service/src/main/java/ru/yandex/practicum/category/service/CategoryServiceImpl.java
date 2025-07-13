package ru.yandex.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dao.CategoryRepository;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;
import ru.yandex.practicum.category.dto.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto add(CategoryDtoPost newCategory) {
        checkCategoryNameExists(newCategory.getName());
        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategory));
        log.info("Category was created: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        return categoryRepository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = getCategoryOrThrow(id);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDtoPost newCategory) {
        Category existingCategory = getCategoryOrThrow(id);

        if (!existingCategory.getName().equals(newCategory.getName())) {
            checkCategoryNameExists(newCategory.getName());
        }

        existingCategory.setName(newCategory.getName());

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category was updated with id={}, old name='{}', new name='{}'",
                id, existingCategory.getName(), newCategory.getName());
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = getCategoryOrThrow(id);

        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException(String.format("Cannot delete category with id=%d because it has linked events", id));
        }

        categoryRepository.deleteById(id);
        log.info("Category was deleted: {}", category);
    }

    private void checkCategoryNameExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateException("Category already exists: " + name);
        }
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
    }
}
