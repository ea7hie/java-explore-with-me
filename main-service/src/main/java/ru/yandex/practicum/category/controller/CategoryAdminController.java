package ru.yandex.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.CategoryDtoPost;
import ru.yandex.practicum.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody CategoryDtoPost category) {
        log.info("POST /admin/categories - Add category: {}", category);
        return categoryService.add(category);
    }

    @PatchMapping("/{id}")
    public CategoryDto update(@PathVariable("id") Long id, @Valid @RequestBody CategoryDtoPost category) {
        log.info("PATCH /admin/categories/{} - Update category", id);
        return categoryService.update(id, category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("DELETE /admin/categories/{} - Delete category", id);
        categoryService.delete(id);
    }
}
