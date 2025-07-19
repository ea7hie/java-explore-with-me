package ru.yandex.practicum.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @Positive long userId,
                                              @RequestParam @Positive long eventId) {
        log.info("user with id={} send request to event with id={}", userId, eventId);
        return requestService.makeRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> addRequest(@PathVariable @Positive long userId) {
        log.info("user with id={} get all own requests", userId);
        return requestService.getOwnRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto deleteRequest(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("user with id={} delete request with id={}", userId, requestId);
        return requestService.deleteRequest(userId, requestId);
    }
}
