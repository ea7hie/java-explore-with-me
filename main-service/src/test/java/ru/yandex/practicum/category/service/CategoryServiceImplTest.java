package ru.yandex.practicum.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.category.dao.CategoryRepository;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.DuplicateException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryDtoPost newCategoryDto;
    private static final Long EXISTING_ID = 1L;
    private static final String EXISTING_NAME = "Test Category";

    @BeforeEach
    void setup() {
        testCategory = new Category();
        testCategory.setId(EXISTING_ID);
        testCategory.setName(EXISTING_NAME);

        newCategoryDto = new CategoryDtoPost();
        newCategoryDto.setName("New Category");
    }

    @Test
    void addCategory_Success() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDto addedCategory = categoryService.add(newCategoryDto);

        verify(categoryRepository, times(1)).existsByName(newCategoryDto.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));

        assertNotNull(addedCategory);
    }

    @Test
    void addCategory_DuplicateName() {
        when(categoryRepository.existsByName(newCategoryDto.getName())).thenReturn(true);

        assertThrows(DuplicateException.class, () -> categoryService.add(newCategoryDto));
    }

    @Test
    void findAll_Pagination() {
        List<Category> categories = List.of(testCategory, new Category());
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDto> result = categoryService.findAll(0, 2);

        assertEquals(2, result.size());
    }

    @Test
    void updateCategory_NonUniqueName() {
        when(categoryRepository.findById(EXISTING_ID)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Duplicate Name")).thenReturn(true);

        CategoryDtoPost conflictingDto = new CategoryDtoPost();
        conflictingDto.setName("Duplicate Name");

        assertThrows(DuplicateException.class, () ->
                categoryService.update(EXISTING_ID, conflictingDto)
        );
    }

    @Test
    void deleteCategory_Success() {
        when(eventRepository.existsByCategoryId(EXISTING_ID)).thenReturn(false);
        when(categoryRepository.findById(EXISTING_ID)).thenReturn(Optional.ofNullable(testCategory));

        categoryService.delete(EXISTING_ID);

        verify(categoryRepository, times(1)).deleteById(EXISTING_ID);
    }

    @Test
    void deleteCategory_Conflict() {
        when(eventRepository.existsByCategoryId(EXISTING_ID)).thenReturn(true);
        when(categoryRepository.findById(EXISTING_ID)).thenReturn(Optional.ofNullable(testCategory));

        assertThrows(ConflictException.class, () -> categoryService.delete(EXISTING_ID));
    }
}