package ru.yandex.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.service.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.add(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateComp(@PathVariable("compId") Long compId,
                                     @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long compId) {
        compilationService.delete(compId);
    }
}
