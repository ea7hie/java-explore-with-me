package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.in.UpdateEventAdminRequest;
import ru.yandex.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> findEvents(List<Long> users, List<State> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  int from, int size);

    EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
