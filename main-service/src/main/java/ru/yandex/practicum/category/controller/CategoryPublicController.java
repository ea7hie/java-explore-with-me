package ru.yandex.practicum.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /categories - Getting categories");
        return service.findAll(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable("id") @Positive Long id) {
        log.info("GET /categories/{} - Getting category by id", id);
        return service.findById(id);
    }
}
