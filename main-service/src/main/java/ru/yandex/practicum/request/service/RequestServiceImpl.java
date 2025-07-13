package ru.yandex.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.OperationNotAllowedException;
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
            throw new OperationNotAllowedException("Initiator cannot send request to own event");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new OperationNotAllowedException("This event have not been published yet");
        }

        Optional<Request> optionalRequest = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (optionalRequest.isPresent()) {
            throw new OperationNotAllowedException("Request have been already send");
        }

        long confirmedRequests = requestRepository.getConfirmedRequests(eventId, Status.CONFIRMED);
        if (confirmedRequests >= event.getParticipantLimit()) {
            throw new OperationNotAllowedException("Cannot send request because limit has been reached");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(event.getRequestModeration() ? Status.PENDING : Status.CONFIRMED);
        request.setCreated(LocalDateTime.now());

        Request saved = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(saved);
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
        request.setStatus(Status.REJECTED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));
    }
}
