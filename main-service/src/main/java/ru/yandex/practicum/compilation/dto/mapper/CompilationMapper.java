package ru.yandex.practicum.compilation.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toCompilationDto(Compilation compilation, List<EventFullDto> eventFullDtos) {
        return new CompilationDto(eventFullDtos, compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(-1L, events, newCompilationDto.getPinned(), newCompilationDto.getTitle());
    }
}