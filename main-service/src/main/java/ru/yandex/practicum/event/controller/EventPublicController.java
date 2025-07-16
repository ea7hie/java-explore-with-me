package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.model.Sort;
import ru.yandex.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventShortDto> findEventsByText(@RequestParam(defaultValue = "") String text,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) String rangeStart,
                                                @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                @RequestParam(required = false) String sort,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        log.info("search by text {}. client ip: {}", text, ip);
        log.info("search by text {}. endpoint path: {}", text, uri);

        LocalDateTime start = (rangeStart == null) ? null : LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = (rangeEnd == null) ? null : LocalDateTime.parse(rangeEnd, formatter);
        Sort sorting = sort == null ? Sort.EVENT_DATE : Sort.valueOf(sort);
        if (text.equals("0")) {
            text = "";
        }
        if (categories == null || (categories.size() == 1 && categories.contains(0L))) {
            categories = List.of();
        }
        return eventService.findEventsByText(text, categories, paid, start, end,
                onlyAvailable, sorting, from, size, ip, uri);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventsById(@PathVariable @Positive long eventId, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        log.info("get event by id {}. client ip: {}", eventId, ip);
        log.info("get event by id {}. endpoint path: {}", eventId, uri);
        return eventService.getEvent(eventId, ip, uri);
    }
}
