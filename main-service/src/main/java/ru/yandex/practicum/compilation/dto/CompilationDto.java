package ru.yandex.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.dto.get.EventFullDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    List<EventFullDto> events;
    Long id;
    Boolean pinned;
    String title;
}