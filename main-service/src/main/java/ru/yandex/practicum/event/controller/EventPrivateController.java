package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.NewEventDto;
import ru.yandex.practicum.event.dto.in.UpdateEventUserRequest;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@RequestBody @Valid NewEventDto newEventDto,
                                       @PathVariable @Positive long userId) {
        log.info("user with id={} added new event={}", userId, newEventDto);
        return eventService.create(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> findEventsByInitiatorId(@Positive @PathVariable long userId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                       @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("search events by initiatorId={}", userId);
        return eventService.findEventsByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventByInitiatorIdAndEventId(@PathVariable @Positive long userId,
                                                         @PathVariable @Positive long eventId) {
        log.info("search event by initiatorId={} and eventId={}", userId, eventId);
        return eventService.findEventByInitiatorIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventsByInitiatorIdAndEventId(@PathVariable @Positive long userId,
                                                            @PathVariable @Positive long eventId,
                                                            @RequestBody @Valid UpdateEventUserRequest updateEvent) {
        log.info("update event by initiatorId={} and eventId={}", userId, eventId);
        return eventService.updateEventsByInitiatorIdAndEventId(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findWhoSendRequestToEvent(@PathVariable @Positive long userId,
                                                                   @PathVariable @Positive long eventId) {
        log.info("search requesters to event by initiatorId={} and eventId={}", userId, eventId);
        return eventService.getListRequestsToEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeStatusRequestToEvent(@PathVariable @Positive long userId,
                                                                     @PathVariable @Positive long eventId,
                                                                     @RequestBody @Valid EventRequestStatusUpdateRequest dto) {
        log.info("change status for requests initiatorId={} and eventId={} for users={}", userId, eventId, dto);
        return eventService.changeStatusRequestToEvent(userId, eventId, dto);
    }
}
