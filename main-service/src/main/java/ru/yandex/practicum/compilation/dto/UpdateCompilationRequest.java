package ru.yandex.practicum.compilation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    @UniqueElements
    List<Long> events;

    Boolean pinned;

    @Length(min = 1, max = 50)
    String title;
}