package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.NewEventDto;
import ru.yandex.practicum.event.dto.in.UpdateEventAdminRequest;
import ru.yandex.practicum.event.dto.in.UpdateEventUserRequest;
import ru.yandex.practicum.event.model.Sort;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> findEvents(List<Long> users, List<State> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  int from, int size);

    EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

        List<EventShortDto> findEventsByText(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         boolean onlyAvailable, Sort sort, int from, int size,
                                         String ip, String uri);

    EventFullDto getEvent(long eventId, String ip, String uri);

    List<EventShortDto> findEventsByInitiatorId(long userId, int from, int size);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto findEventByInitiatorIdAndEventId(long userId, long eventId);

    EventFullDto updateEventsByInitiatorIdAndEventId(long userId, long eventId, UpdateEventUserRequest updateEvent);

    List<ParticipationRequestDto> getListRequestsToEvent(long userId, long eventId);

    EventRequestStatusUpdateResult changeStatusRequestToEvent(long userId, long eventId,
                                                              EventRequestStatusUpdateRequest dto);
}
