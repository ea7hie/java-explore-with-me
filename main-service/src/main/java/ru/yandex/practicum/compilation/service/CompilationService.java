package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDto add(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);

    void delete(Long compId);
}
