package ru.yandex.practicum.category.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryPublicController.class)
public class CategoryPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        reset(service);
    }

    @Test
    void getCategories_Pagination_ReturnsList() throws Exception {
        List<CategoryDto> mockCategories = Arrays.asList(
                new CategoryDto(1L, "Books"),
                new CategoryDto(2L, "Electronics")
        );

        when(service.findAll(0, 10)).thenReturn(mockCategories);

        mockMvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Books"));

        verify(service).findAll(0, 10);
    }

    @Test
    void getCategories_DefaultParams() throws Exception {
        when(service.findAll(0, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service).findAll(0, 10);
    }

    @Test
    void getCategoryById_WithValidId_ReturnsCategory() throws Exception {
        Long validId = 1L;
        CategoryDto mockCategory = new CategoryDto(validId, "Music");

        given(service.findById(validId)).willReturn(mockCategory);

        mockMvc.perform(get("/categories/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Music"));

        verify(service).findById(validId);
    }

    @Test
    void getCategoryById_NonExistingId_Returns404() throws Exception {
        Long invalidId = 999L;

        given(service.findById(invalidId)).willThrow(new NotFoundException("Category not found"));

        mockMvc.perform(get("/categories/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCategoryById_NegativeId_Returns400() throws Exception {
        given(service.findById(-5L)).willThrow(new NotFoundException("Category not found"));

        mockMvc.perform(get("/categories/-5"))
                .andExpect(status().isBadRequest());
    }
}