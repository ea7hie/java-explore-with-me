package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dao.CategoryRepository;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.in.UpdateEventAdminRequest;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.location.Location;
import ru.yandex.practicum.event.location.LocationDto;
import ru.yandex.practicum.event.location.LocationMapper;
import ru.yandex.practicum.event.location.LocationRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.event.model.StateActionForAdmin;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dao.RequestRepository;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.Status;
import ru.yandex.practicum.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsService statsService;
    private final LocationRepository locationRepository;


    @Override
    public List<EventFullDto> findEvents(List<Long> users, List<State> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         int from, int size) {

        List<Request> allRequests = getRequestsFilterByUserIds(users);
        List<Event> distinctEvents = getDistinctEventsFromRequests(allRequests);
        distinctEvents = getEventsFilterByStates(states, distinctEvents);
        distinctEvents = getEventsFilterByCategories(categories, distinctEvents);
        distinctEvents = getEventsFilterByRangeStart(rangeStart, distinctEvents);
        distinctEvents = getEventsFilterByRangeEnd(rangeEnd, distinctEvents);
        distinctEvents = getEventsInAmount(distinctEvents, from, size);

        Map<Long, Long> eventsView = statsService.getEventsView(distinctEvents);

        return distinctEvents.stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        requestRepository.getConfirmedRequests(event.getId(), Status.CONFIRMED),
                        eventsView.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event oldEventForUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        oldEventForUpdate.setAnnotation(updateEventAdminRequest.getAnnotation());
        oldEventForUpdate.setCategory(
                getNewCategoryForUpdatingEvent(oldEventForUpdate.getCategory(), updateEventAdminRequest.getCategory())
        );
        oldEventForUpdate.setDescription(updateEventAdminRequest.getDescription());
        oldEventForUpdate.setEventDate(
                getNewEventDateForUpdatingEvent(oldEventForUpdate.getEventDate(), updateEventAdminRequest.getEventDate())
        );
        oldEventForUpdate.setLocation(getNewLocationForUpdatingEvent(
                oldEventForUpdate.getLocation(), updateEventAdminRequest.getLocation())
        );
        oldEventForUpdate.setPaid(
                getNewPaidForUpdatingEvent(oldEventForUpdate.getPaid(), updateEventAdminRequest.getPaid())
        );
        oldEventForUpdate.setParticipantLimit(getNewParticipantLimitForUpdatingEvent(
                oldEventForUpdate.getParticipantLimit(), updateEventAdminRequest.getParticipantLimit())
        );
        oldEventForUpdate.setRequestModeration(getNewRequestModerationForUpdatingEvent(
                oldEventForUpdate.getRequestModeration(), updateEventAdminRequest.getRequestModeration())
        );
        oldEventForUpdate.setTitle(updateEventAdminRequest.getTitle());

        return EventMapper.toEventFullDto(
                getEventWithUpdatedState(updateEventAdminRequest.getStateAction(), oldEventForUpdate),
                requestRepository.getConfirmedRequests(oldEventForUpdate.getId(), Status.CONFIRMED),
                statsService.getEventsView(List.of(oldEventForUpdate)).get(oldEventForUpdate.getId())
        );
    }

    private List<Request> getRequestsFilterByUserIds(List<Long> users) {
        return users.isEmpty() ? requestRepository.findAllByStatus(Status.CONFIRMED)
                : requestRepository.findByRequesterIdInAndStatus(users, Status.CONFIRMED);
    }

    private List<Event> getDistinctEventsFromRequests(List<Request> allRequests) {
        return allRequests.stream()
                .map(Request::getEvent)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Event> getEventsFilterByStates(List<State> states, List<Event> distinctEvents) {
        return states.isEmpty() ? distinctEvents
                : distinctEvents.stream()
                .filter(event -> states.contains(event.getState()))
                .collect(Collectors.toList());
    }

    private List<Event> getEventsFilterByCategories(List<Long> categories, List<Event> distinctEvents) {
        if (categories.isEmpty()) {
            return distinctEvents;
        }

        List<Category> categoryList = categoryRepository.findAllById(categories);
        return distinctEvents.stream()
                .filter(event -> categoryList.contains(event.getCategory()))
                .collect(Collectors.toList());
    }

    private List<Event> getEventsFilterByRangeStart(LocalDateTime rangeStart, List<Event> distinctEvents) {
        return rangeStart == null ? distinctEvents
                : distinctEvents.stream()
                .filter(event -> rangeStart.isBefore(event.getEventDate()))
                .collect(Collectors.toList());
    }

    private List<Event> getEventsFilterByRangeEnd(LocalDateTime rangeEnd, List<Event> distinctEvents) {
        return rangeEnd == null ? distinctEvents
                : distinctEvents.stream()
                .filter(event -> event.getEventDate().isBefore(rangeEnd))
                .collect(Collectors.toList());
    }

    private List<Event> getEventsInAmount(List<Event> distinctEvents, int from, int size) {
        return distinctEvents.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    private Category getNewCategoryForUpdatingEvent(Category oldCategory, long newCategory) {
        return (oldCategory.getId() == newCategory || newCategory == 0L) ? oldCategory
                : categoryRepository.findById(newCategory)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id=%d was not found", newCategory))
                );
    }

    private LocalDateTime getNewEventDateForUpdatingEvent(LocalDateTime oldEventDate, LocalDateTime newEventDate) {
        return (oldEventDate.isEqual(newEventDate) || newEventDate == null) ? oldEventDate : newEventDate;
    }

    private Location getNewLocationForUpdatingEvent(Location oldLocation, LocationDto newLocationDto) {
        LocationDto oldLocationDto = LocationMapper.toLocationDto(oldLocation);
        return (oldLocationDto.equals(newLocationDto) || newLocationDto == null)
                ? oldLocation
                : locationRepository.findByLatAndLon(newLocationDto.getLat(), newLocationDto.getLon())
                .orElseThrow(() -> new NotFoundException(String.format("Location with lat=%f and lon=%f was not found",
                        newLocationDto.getLat(), newLocationDto.getLon())));
    }

    private Boolean getNewPaidForUpdatingEvent(Boolean oldPaid, Boolean newPaid) {
        return newPaid == null ? oldPaid : newPaid;
    }

    private Integer getNewParticipantLimitForUpdatingEvent(Integer oldParticipantLimit, Integer newParticipantLimit) {
        return (oldParticipantLimit.equals(newParticipantLimit) || newParticipantLimit == null) ? oldParticipantLimit
                : newParticipantLimit;
    }

    private Boolean getNewRequestModerationForUpdatingEvent(Boolean oldRequestModeration, Boolean newRequestModeration) {
        return newRequestModeration == null ? oldRequestModeration : newRequestModeration;
    }

    private Event getEventWithUpdatedState(StateActionForAdmin stateActionForAdmin, Event eventForUpdate) {
        if (stateActionForAdmin == StateActionForAdmin.REJECT_EVENT) {
            eventForUpdate.setState(State.CANCELLED);
            return eventForUpdate;
        }

        eventForUpdate.setState(State.PUBLISHED);
        eventForUpdate.setPublishedOn(LocalDateTime.now());
        return eventForUpdate;
    }
}
