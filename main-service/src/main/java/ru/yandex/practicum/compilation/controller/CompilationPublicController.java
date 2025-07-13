package ru.yandex.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.compilation.service.CompilationService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

}
