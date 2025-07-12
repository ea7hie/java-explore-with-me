package ru.yandex.practicum.event.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.category.dto.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.NewEventDto;
import ru.yandex.practicum.event.dto.in.UpdateEventUserRequest;
import ru.yandex.practicum.event.location.Location;
import ru.yandex.practicum.event.location.LocationMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.user.dto.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public EventShortDto toEventShortDto(Event event, long confirmedRequests, long viewes) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests,
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                viewes
        );
    }

    public Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return new Event(
                -1L,
                newEventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                user,
                location,
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.getRequestModeration(),
                State.PENDING,
                newEventDto.getTitle()
        );
    }

    public EventFullDto toEventFullDto(Event event, long confirmedRequests, long viewes) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                viewes
        );
    }

    public Event toEvent(UpdateEventUserRequest updateEventUserRequest, Category category, User user,
                         long id, long locationId, LocalDateTime createdOn) {
        return new Event(
                id,
                updateEventUserRequest.getAnnotation(),
                category,
                createdOn,
                updateEventUserRequest.getDescription(),
                updateEventUserRequest.getEventDate(),
                user,
                LocationMapper.toLocation(updateEventUserRequest.getLocation(), locationId),
                updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit(),
                null,
                updateEventUserRequest.getRequestModeration(),
                State.PENDING,
                updateEventUserRequest.getTitle()
        );
    }
}
