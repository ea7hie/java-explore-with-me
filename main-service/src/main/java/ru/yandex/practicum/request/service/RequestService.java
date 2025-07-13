package ru.yandex.practicum.request.service;

import ru.yandex.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto makeRequest(long userId, long eventId);

    List<ParticipationRequestDto> getOwnRequests(long userId);

    ParticipationRequestDto deleteRequest(long userId, long requestId);
}
