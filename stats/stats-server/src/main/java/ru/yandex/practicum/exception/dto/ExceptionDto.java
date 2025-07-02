package ru.yandex.practicum.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionDto {
    private final String error;
    private final String description;
}