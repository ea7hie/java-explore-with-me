package ru.yandex.practicum.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.exception.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryAdminController.class)
public class CategoryAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createCategory_ValidInput_Returns201() throws Exception {
        CategoryDtoPost categoryPost = new CategoryDtoPost("TestCategory");
        CategoryDto expectedResponse = new CategoryDto(1L, "TestCategory");

        when(categoryService.add(any())).thenReturn(expectedResponse);

        mockMvc.perform(post("/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryPost)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(categoryService).add(categoryPost);
    }

    @Test
    void createCategory_InvalidName_Returns400() throws Exception {
        CategoryDtoPost invalidCategory = new CategoryDtoPost("");

        mockMvc.perform(post("/admin/category")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).add(any());
    }

    @Test
    void updateCategory_WithValidId_ReturnsUpdatedCategory() throws Exception {
        Long existingCategoryId = 1L;
        CategoryDtoPost updateData = new CategoryDtoPost("NewName");
        CategoryDto expectedResult = new CategoryDto(existingCategoryId, "NewName");

        when(categoryService.update(existingCategoryId, updateData)).thenReturn(expectedResult);

        mockMvc.perform(patch("/admin/category/{id}", existingCategoryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(categoryService).update(existingCategoryId, updateData);
    }

    @Test
    void deleteCategory_ExistingCategory_ReturnsNoContent() throws Exception {
        Long categoryIdToDelete = 1L;

        mockMvc.perform(delete("/admin/category/{id}", categoryIdToDelete))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(categoryIdToDelete);
    }

    @Test
    void deleteCategory_NonExistingId_Returns404() throws Exception {
        Long nonExistingId = 999L;
        doThrow(new NotFoundException("Category not found")).when(categoryService).delete(nonExistingId);

        mockMvc.perform(delete("/admin/category/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategory_InvalidCategoryData_Returns400() throws Exception {
        mockMvc.perform(patch("/admin/category/5")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDtoPost(""))))
                .andExpect(status().isBadRequest());
    }
}