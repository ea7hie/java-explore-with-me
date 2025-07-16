package ru.yandex.practicum.event.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dao.CategoryRepository;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.UpdateEventAdminRequest;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.Sort;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.event.model.StateActionForAdmin;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.location.Location;
import ru.yandex.practicum.location.LocationDto;
import ru.yandex.practicum.location.LocationMapper;
import ru.yandex.practicum.location.LocationRepository;
import ru.yandex.practicum.request.dao.RequestRepository;
import ru.yandex.practicum.request.model.Status;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private EventServiceImpl service;

    @Mock
    private RequestRepository requestRepository;

    private List<Event> testEvents;

    private Event testEvent;
    private Category testCategory;
    private Location testLocation;

    @BeforeEach
    void setup() {
        testCategory = new Category(42L, "Music");
        testLocation = new Location(1L, 55.751244F, 37.618423F);

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setCategory(testCategory);
        testEvent.setLocation(testLocation);
        testEvent.setEventDate(LocalDateTime.now().plusDays(2));
        testEvent.setState(State.PENDING);

        testEvents = List.of(
                createTestEvent(1L, State.PUBLISHED, 100L, 1L, LocalDateTime.now().plusDays(1)),
                createTestEvent(2L, State.CANCELED, 50L, 2L, LocalDateTime.now().minusDays(1)),
                createTestEvent(3L, State.PENDING, 200L, 3L, LocalDateTime.now().plusMonths(1))
        );
    }

    @Test
    public void newCategory_NotFound() {
        Long nonExistentCategoryId = 999L;
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setCategory(nonExistentCategoryId);

        when(categoryRepository.findById(nonExistentCategoryId)).thenThrow(new NotFoundException(
                "Category with id=999 was not found"
        ));

        assertThatThrownBy(() ->
                getNewCategoryForUpdatingEvent(testEvent.getCategory(), nonExistentCategoryId)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Category with id=999 was not found");
    }

    @Test
    public void updateLocation_New() {
        LocationDto newDto = new LocationDto(55.8f, 37.8f);

        when(locationRepository.findByLatAndLon(55.8f, 37.8f)).thenReturn(Optional.empty());
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> {
            Location loc = inv.getArgument(0);
            loc.setId(2L);
            return loc;
        });

        Location newLocation = saveNewLocationOrGetAddedBefore(newDto);
        assertNotNull(newLocation.getId());
    }

    @Test
    public void getEventOrThrow_NotFound() {
        long missingId = 999L;
        when(eventRepository.findById(missingId)).thenThrow(new NotFoundException(
                "Event with id=999 was not found"));

        assertThatThrownBy(() -> getEventOrThrow(missingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith("Event with id=999 was not found");
    }

    @Test
    void getEventWithUpdatedStateTest() {
        testEvent.setState(State.PENDING);
        Event updated = getEventWithUpdatedState(StateActionForAdmin.PUBLISH_EVENT, testEvent);
        assertEquals(State.PUBLISHED, updated.getState());
        assertNotNull(updated.getPublishedOn());

        testEvent.setState(State.PENDING);
        Event rejected = getEventWithUpdatedState(StateActionForAdmin.REJECT_EVENT, testEvent);
        assertEquals(State.CANCELED, rejected.getState());
    }

    @Test
    void findAllWhenNoFilters() {
        when(eventRepository.findAll()).thenReturn(testEvents);
        when(statsService.getEventsView(any())).thenReturn(Map.of());

        List<EventFullDto> result = service.findEvents(null, null, null, null, null, 0, 10);

        assertEquals(testEvents.size(), result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void dateRangeFilter() {
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(2);
        LocalDateTime rangeEnd = LocalDateTime.now().plusDays(2);

        List<Event> filtered = testEvents.stream()
                .filter(e -> e.getEventDate().isAfter(rangeStart) &&
                        e.getEventDate().isBefore(rangeEnd))
                .collect(Collectors.toList());

        when(eventRepository.findAll()).thenReturn(testEvents);
        when(statsService.getEventsView(any())).thenReturn(Map.of());

        List<EventFullDto> result = service.findEvents(
                null, null, null, rangeStart, rangeEnd, 0, 10);

        assertEquals(filtered.size(), result.size());
    }

    @Test
    void pagination() {
        when(eventRepository.findAll()).thenReturn(testEvents);
        when(statsService.getEventsView(any())).thenReturn(Map.of());

        List<EventFullDto> result = service.findEvents(
                null, null, null, null, null, 1, 1);

        assertEquals(1, result.size());
        assertEquals(testEvents.get(1).getId(), result.get(0).getId());
    }

    @Test
    void testFindAllEventsWhenNoText() {
        Event event = createTestEvent(1L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(2));
        Event event1 = createTestEvent(2L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(1));

        List<Event> publishedEvents = List.of(event, event1);
        when(eventRepository.findAllByState(State.PUBLISHED)).thenReturn(publishedEvents);

        List<EventShortDto> result = service.findEventsByText("", null, null, null, null, false, null, 0, 10, "ip", "uri");

        verify(eventRepository).findAllByState(State.PUBLISHED);
        assertThat(result).hasSize(2);
    }

    @Test
    void testFindEventsByTextFilter() {
        String text = "concert";
        List<Event> matchingEvents = List.of(new Event(), new Event());
        when(eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text))
                .thenReturn(matchingEvents);

        service.findEventsByText(text, null, null, null, null, false, null, 0, 10, "ip", "uri");

        verify(eventRepository).findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text);
    }

    @Test
    void testFilterByCategories() {
        Event event = createTestEvent(1L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(2));
        Event event1 = createTestEvent(2L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(1));
        Event event2 = createTestEvent(3L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(1));

        List<Long> categories = List.of(1L);
        List<Event> allEvents = List.of(event, event1, event2);
        when(eventRepository.findAllByState(State.PUBLISHED)).thenReturn(allEvents);

        service.findEventsByText("", categories, null, null, null, false, null, 0, 10, "ip", "uri");

        verify(eventRepository).findAllByState(State.PUBLISHED);
    }

    @Test
    void testSortByEventDate() {
        Event event = createTestEvent(1L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(2));
        Event event1 = createTestEvent(2L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(1));
        List<Event> events = Arrays.asList(event, event1);
        when(eventRepository.findAllByState(any())).thenReturn(events);

        List<EventShortDto> result = service.findEventsByText("", null, null, null,
                null, false, Sort.EVENT_DATE, 0, 2, "ip", "uri");

        verify(eventRepository).findAllByState(any());
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEventDate()).isBefore(result.get(1).getEventDate());
    }

    @Test
    void testGetExistingPublishedEvent() {
        Event event = createTestEvent(1L, State.PUBLISHED, 1L, 1L, LocalDateTime.now().plusDays(2));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.getConfirmedRequests(1L, Status.CONFIRMED)).thenReturn(5L);
        Map<Long, Long> views = new HashMap<>();
        views.put(1L, 100L);
        when(statsService.getEventsView(anyList())).thenReturn(views);

        EventFullDto result = service.getEvent(1L, "ip", "uri");

        assertThat(result.getConfirmedRequests()).isEqualTo(5L);
        assertThat(result.getViews()).isEqualTo(99L);
        verify(statsService).sendHit(eq("uri"), eq("ip"));
    }

    @Test
    void testGetNonExistentEvent() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.getEvent(2L, "ip", "uri"));
    }

    @Test
    void testGetUnpublishedEvent() {
        Event unpublished = new Event();
        unpublished.setId(2L);
        unpublished.setState(State.CANCELED);
        when(eventRepository.findById(2L)).thenReturn(Optional.of(unpublished));

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.getEvent(2L, "ip", "uri"));
    }

    private Event createTestEvent(Long id, State state, Long initiatorId, Long categoryId, LocalDateTime eventDate) {
        Event event = new Event();
        event.setId(id);
        event.setState(state);
        event.setInitiator(new User());
        event.getInitiator().setId(initiatorId);
        Category category = new Category();
        category.setId(categoryId);
        event.setCategory(category);
        event.setEventDate(eventDate);
        event.setLocation(new Location());
        return event;
    }

    private Event getEventOrThrow(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Event getEventWithUpdatedState(StateActionForAdmin stateActionForAdmin, Event eventForUpdate) {
        if (stateActionForAdmin == StateActionForAdmin.REJECT_EVENT) {
            if (eventForUpdate.getState() == State.PUBLISHED) {
                throw new ConflictException(
                        "Cannot publish the event because it's not in the right state: PUBLISHED");
            }

            eventForUpdate.setState(State.CANCELED);
            return eventForUpdate;
        }

        eventForUpdate.setState(State.PUBLISHED);
        eventForUpdate.setPublishedOn(LocalDateTime.now());
        return eventForUpdate;
    }

    private Category getNewCategoryForUpdatingEvent(Category oldCategory, Long newCategory) {
        return (newCategory == null || Objects.equals(oldCategory.getId(), newCategory) || newCategory == 0L) ? oldCategory
                : categoryRepository.findById(newCategory)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id=%d was not found", newCategory))
                );
    }

    private Location saveNewLocationOrGetAddedBefore(LocationDto newLocationDto) {
        Optional<Location> opt = locationRepository.findByLatAndLon(newLocationDto.getLat(), newLocationDto.getLon());
        return opt.orElseGet(() -> locationRepository.save(LocationMapper.toLocation(newLocationDto, -1L)));
    }
}