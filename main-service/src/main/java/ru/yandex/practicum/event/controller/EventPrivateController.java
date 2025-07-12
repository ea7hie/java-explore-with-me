package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.NewEventDto;
import ru.yandex.practicum.event.dto.in.UpdateEventUserRequest;
import ru.yandex.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@RequestBody @Valid NewEventDto newEventDto,
                                       @PathVariable @Positive long userId) {
        log.info("user with id={} added new event={}", userId, newEventDto);
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEventsByInitiatorId(@Positive @PathVariable long userId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                       @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("search events by initiatorId={}", userId);
        return eventService.findEventsByInitiatorId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventByInitiatorIdAndEventId(@PathVariable @Positive long userId,
                                                         @PathVariable @Positive long eventId) {
        log.info("search events by initiatorId={} and eventId={}", userId, eventId);
        return eventService.findEventByInitiatorIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventsByInitiatorIdAndEventId(@PathVariable @Positive long userId,
                                                            @PathVariable @Positive long eventId,
                                                            @RequestBody @Valid UpdateEventUserRequest updateEvent) {
        log.info("search events by initiatorId={} and eventId={}", userId, eventId);
        return eventService.updateEventsByInitiatorIdAndEventId(userId, eventId, updateEvent);
    }


/*
    @GetMapping("/{eventId}")
    public EventFullDto findEventsByText(@PathVariable long eventId, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        log.info("get event by id {}. client ip: {}", eventId, ip);
        log.info("get event by id {}. endpoint path: {}", eventId, uri);
        return eventService.getEvent(eventId, ip, uri);
    }*/
}
