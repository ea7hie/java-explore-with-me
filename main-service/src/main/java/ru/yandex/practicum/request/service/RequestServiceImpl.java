package ru.yandex.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dao.RequestRepository;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.dto.mapper.RequestMapper;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.Status;
import ru.yandex.practicum.user.dao.UserRepository;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto makeRequest(long userId, long eventId) {
        User requester = getUserOrThrow(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("Initiator cannot send request to own event");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("This event have not been published yet");
        }

        Optional<Request> optionalRequest = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (optionalRequest.isPresent()) {
            throw new ConflictException("Request have been already send");
        }

        if (event.getParticipantLimit() != 0) {
            long confirmedRequests = requestRepository.getConfirmedRequests(eventId, Status.CONFIRMED);
            if (confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictException("Cannot send request because limit has been reached");
            }
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(new Request(-1L, event, requester,
                (!event.getRequestModeration() || event.getParticipantLimit() == 0) ? Status.CONFIRMED
                        : Status.PENDING,
                LocalDateTime.now())));
    }

    @Override
    public List<ParticipationRequestDto> getOwnRequests(long userId) {
        getUserOrThrow(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto deleteRequest(long userId, long requestId) {
        User requester = getUserOrThrow(userId);
        Optional<Request> opt = requestRepository.findById(requestId);
        if (opt.isEmpty()) {
            throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
        }
        Request request = opt.get();
        if (request.getStatus() == Status.CONFIRMED) {
            throw new ConflictException(String.format("Request with id=%d have been already confirmed", requestId));
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));
    }
}
