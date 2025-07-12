package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dao.CategoryRepository;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.comparators.EventComparatorByEventDate;
import ru.yandex.practicum.event.comparators.EventShortDtoComparatorByViews;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.NewEventDto;
import ru.yandex.practicum.event.dto.in.UpdateEventAdminRequest;
import ru.yandex.practicum.event.dto.in.UpdateEventUserRequest;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.model.*;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.OperationNotAllowedException;
import ru.yandex.practicum.location.Location;
import ru.yandex.practicum.location.LocationDto;
import ru.yandex.practicum.location.LocationMapper;
import ru.yandex.practicum.location.LocationRepository;
import ru.yandex.practicum.request.dao.RequestRepository;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.Status;
import ru.yandex.practicum.user.dao.UserRepository;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    //ADMIN
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
                        getConfirmedRequests(event),
                        eventsView.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event oldEventForUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (LocalDateTime.now().plusHours(1).isBefore(oldEventForUpdate.getEventDate())) {
            throw new OperationNotAllowedException(
                    "Cannot publish the event because it's eventDate is less than an hour away");
        }

        if (oldEventForUpdate.getState() != State.PENDING) {
            throw new OperationNotAllowedException(
                    "Cannot publish the event because it's not in the right state: " + oldEventForUpdate.getState());
        }

        oldEventForUpdate = getEventWithUpdatedState(updateEventAdminRequest.getStateAction(), oldEventForUpdate);

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

        oldEventForUpdate = eventRepository.save(oldEventForUpdate);

        return EventMapper.toEventFullDto(oldEventForUpdate,
                requestRepository.getConfirmedRequests(oldEventForUpdate.getId(), Status.CONFIRMED),
                statsService.getEventsView(List.of(oldEventForUpdate)).get(oldEventForUpdate.getId())
        );
    }

    //PUBLIC
    @Override
    public List<EventShortDto> findEventsByText(String text, List<Long> categories, boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                boolean onlyAvailable, Sort sort, int from, int size,
                                                String ip, String uri) {
        statsService.sendHit(uri, ip);

        List<Event> foundedEvents;

        if (text.isEmpty()) {
            foundedEvents = eventRepository.findAllByState(State.PUBLISHED);
        } else {
            foundedEvents = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text)
                    .stream()
                    .filter(event -> event.getState() == State.PUBLISHED)
                    .collect(Collectors.toList());
        }

        foundedEvents = foundedEvents.stream()
                .distinct()
                .filter(event -> event.getPaid() == paid)
                .collect(Collectors.toList());

        foundedEvents = getEventsFilterByCategories(categories, foundedEvents);

        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;

        foundedEvents = getEventsFilterByRangeStart(rangeStart, foundedEvents);
        foundedEvents = getEventsFilterByRangeEnd(rangeEnd, foundedEvents);

        Map<Event, Long> amountConfirmedRequests = new HashMap<>();
        foundedEvents = foundedEvents.stream()
                .peek(event -> {
                    amountConfirmedRequests.put(event, getConfirmedRequests(event));
                })
                .collect(Collectors.toList());

        if (onlyAvailable) {
            foundedEvents = foundedEvents.stream()
                    .filter(event -> event.getParticipantLimit() < amountConfirmedRequests.get(event))
                    .collect(Collectors.toList());
        }

        Map<Long, Long> eventsView;
        if (sort == Sort.EVENT_DATE) {
            foundedEvents = foundedEvents.stream()
                    .sorted(new EventComparatorByEventDate())
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        } else {
            eventsView = statsService.getEventsView(foundedEvents);
            return foundedEvents.stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            amountConfirmedRequests.get(event),
                            eventsView.get(event.getId())))
                    .sorted(new EventShortDtoComparatorByViews())
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }

        eventsView = statsService.getEventsView(foundedEvents);
        return foundedEvents.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        amountConfirmedRequests.get(event),
                        eventsView.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(long eventId, String ip, String uri) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        statsService.sendHit(uri, ip);

        return EventMapper.toEventFullDto(event,
                getConfirmedRequests(event),
                statsService.getEventsView(List.of(event)).get(eventId));
    }

    //PRIVATE
    @Override
    public List<EventShortDto> findEventsByInitiatorId(long userId, int from, int size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId).stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        Map<Long, Long> eventsView = statsService.getEventsView(events);

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        getConfirmedRequests(event),
                        eventsView.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        if (LocalDateTime.now().plusHours(2).isAfter(newEventDto.getEventDate())) {
            throw new OperationNotAllowedException("должно содержать дату, которая еще не наступила");
        }

        LocationDto newLocationDto = newEventDto.getLocation();
        Optional<Location> opt = locationRepository.findByLatAndLon(newLocationDto.getLat(), newLocationDto.getLon());
        Location newLocation = opt.orElseGet(() ->
                locationRepository.save(LocationMapper.toLocation(newLocationDto, -1L)));

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", newEventDto.getCategory())));

        Event saved = eventRepository.save(EventMapper.toEvent(newEventDto, category, initiator, newLocation));

        return EventMapper.toEventFullDto(saved, 0L, 0L);
    }

    @Override
    public EventFullDto findEventByInitiatorIdAndEventId(long userId, long eventId) {
        Event event = eventRepository.findByInitiatorIdAndEventId(userId, eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d from initiator with id=%d was not found", eventId, userId)));

        return EventMapper.toEventFullDto(event,
                getConfirmedRequests(event),
                statsService.getEventsView(List.of(event)).get(event));
    }

    @Override
    public EventFullDto updateEventsByInitiatorIdAndEventId(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        Event oldEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d from initiator with id=%d was not found", eventId, userId)));

        if (oldEvent.getState() == State.PUBLISHED) {
            throw new OperationNotAllowedException("Only pending or canceled events can be changed");
        }

        if (LocalDateTime.now().plusHours(2).isAfter(oldEvent.getEventDate())) {
            throw new OperationNotAllowedException(
                    "Cannot update the event because it's eventDate is less than an hour away");
        }

        if (updateEvent.getStateAction() == StateActionForUser.CANCEL_REVIEW) {
            oldEvent.setState(State.CANCELLED);
            return EventMapper.toEventFullDto(oldEvent, 0L, 0L);
        }

        oldEvent.setAnnotation(updateEvent.getAnnotation());
        oldEvent.setCategory(
                getNewCategoryForUpdatingEvent(oldEvent.getCategory(), updateEvent.getCategory())
        );
        oldEvent.setDescription(updateEvent.getDescription());
        oldEvent.setEventDate(
                getNewEventDateForUpdatingEvent(oldEvent.getEventDate(), updateEvent.getEventDate())
        );
        oldEvent.setLocation(getNewLocationForUpdatingEvent(
                oldEvent.getLocation(), updateEvent.getLocation())
        );
        oldEvent.setPaid(
                getNewPaidForUpdatingEvent(oldEvent.getPaid(), updateEvent.getPaid())
        );
        oldEvent.setParticipantLimit(getNewParticipantLimitForUpdatingEvent(
                oldEvent.getParticipantLimit(), updateEvent.getParticipantLimit())
        );
        oldEvent.setRequestModeration(getNewRequestModerationForUpdatingEvent(
                oldEvent.getRequestModeration(), updateEvent.getRequestModeration())
        );
        oldEvent.setTitle(updateEvent.getTitle());

        oldEvent = eventRepository.save(oldEvent);

        return EventMapper.toEventFullDto(oldEvent, 0L, 0L);
    }

    //NON-BUSINESS
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

        return distinctEvents.stream()
                .filter(event -> categories.contains(event.getCategory().getId()))
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
        return (oldEventDate.isEqual(newEventDate) || newEventDate == null
                || LocalDateTime.now().minusHours(2).isAfter(newEventDate)) ? oldEventDate : newEventDate;
    }

    private Location getNewLocationForUpdatingEvent(Location oldLocation, LocationDto newLocationDto) {
        LocationDto oldLocationDto = LocationMapper.toLocationDto(oldLocation);
        return (oldLocationDto.equals(newLocationDto) || newLocationDto == null)
                ? oldLocation
                : saveNewLocationOrGetAddedBefore(newLocationDto);
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
            if (eventForUpdate.getState() == State.PUBLISHED) {
                throw new OperationNotAllowedException(
                        "Cannot publish the event because it's not in the right state: PUBLISHED");
            }

            eventForUpdate.setState(State.CANCELLED);
            return eventForUpdate;
        }

        eventForUpdate.setState(State.PUBLISHED);
        eventForUpdate.setPublishedOn(LocalDateTime.now());
        return eventForUpdate;
    }

    private long getConfirmedRequests(Event event) {
        return requestRepository.getConfirmedRequests(event.getId(), Status.CONFIRMED);
    }

    private Location saveNewLocationOrGetAddedBefore(LocationDto newLocationDto) {
        Optional<Location> opt = locationRepository.findByLatAndLon(newLocationDto.getLat(), newLocationDto.getLon());
        return opt.orElseGet(() -> locationRepository.save(LocationMapper.toLocation(newLocationDto, -1L)));
    }
}
