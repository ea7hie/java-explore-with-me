package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.dao.CompilationRepository;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.dto.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventWithConfirmedRequests;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.service.StatsService;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dao.RequestRepository;
import ru.yandex.practicum.request.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsService statsService;

    @Override
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            Compilation saved = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, List.of()));
            return CompilationMapper.toCompilationDto(saved, List.of());
        }

        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

        if (events.size() != newCompilationDto.getEvents().size()) {
            List<Long> foundedIds = events.stream().map(Event::getId).toList();
            for (Long eventId : newCompilationDto.getEvents()) {
                if (!foundedIds.contains(eventId)) {
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                }
            }
        }

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long>  confirmedRequests = getConfirmedRequestsForList(ids);

        Compilation saved = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));
        Map<Long, Long> eventsView = statsService.getEventsView(events);
        List<EventFullDto> eventFullDtos = events.stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventsView.get(event.getId()) == null ? 0L : eventsView.get(event.getId())))
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(saved, eventFullDtos);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation oldComp = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId))
        );

        List<EventFullDto> eventFullDtos;
        if (updateCompilationRequest.getEvents() != null) {
            if (updateCompilationRequest.getEvents().isEmpty()) {
                eventFullDtos = List.of();
                oldComp.setEvents(List.of());
            } else {
                List<Event> newEvents = eventRepository.findAllById(updateCompilationRequest.getEvents());
                oldComp.setEvents(newEvents);

                Map<Long, Long> eventsView = statsService.getEventsView(newEvents);
                List<Long> ids = newEvents.stream().map(Event::getId).collect(Collectors.toList());
                Map<Long, Long>  confirmedRequests = getConfirmedRequestsForList(ids);

                eventFullDtos = newEvents.stream()
                        .map(event -> EventMapper.toEventFullDto(event,
                                confirmedRequests.getOrDefault(event.getId(), 0L),
                                eventsView.get(event.getId()) == null ? 0L : eventsView.get(event.getId())))
                        .collect(Collectors.toList());
            }
        } else {
            List<Event> oldEvents = eventRepository.findAllById(oldComp.getEvents().stream().map(Event::getId).toList());

            Map<Long, Long> eventsView = statsService.getEventsView(oldEvents);
            List<Long> ids = oldEvents.stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long>  confirmedRequests = getConfirmedRequestsForList(ids);

            eventFullDtos = oldEvents.stream()
                    .map(event -> EventMapper.toEventFullDto(event,
                            confirmedRequests.getOrDefault(event.getId(), 0L),
                            eventsView.get(event.getId()) == null ? 0L : eventsView.get(event.getId())))
                    .collect(Collectors.toList());
        }

        if (updateCompilationRequest.getPinned() != null) {
            oldComp.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null) {
            oldComp.setTitle(updateCompilationRequest.getTitle());
        }

        Compilation updated = compilationRepository.save(oldComp);

        return CompilationMapper.toCompilationDto(updated, eventFullDtos);
    }

    @Override
    public void delete(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId))
        );
        compilationRepository.delete(compilation);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, int from, int size) {
        List<Compilation> allCompilations = (pinned == null) ? compilationRepository.findAll()
                : compilationRepository.findAllByPinnedIs(pinned);

        List<Event> finalAllEvents = new ArrayList<>();
        List<Compilation> result = allCompilations.stream()
                .skip(from)
                .limit(size)
                .peek(comp -> {
                    List<Event> events = eventRepository.findAllById(comp.getEvents().stream()
                            .map(Event::getId)
                            .toList());
                    finalAllEvents.addAll(events);
                })
                .toList();

        List<Event> distinctEvents = finalAllEvents.stream().distinct().collect(Collectors.toList());
        Map<Long, Long> eventsView = statsService.getEventsView(distinctEvents);

        List<Long> ids = distinctEvents.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long>  confirmedRequests = getConfirmedRequestsForList(ids);

        List<EventFullDto> eventFullDtos = distinctEvents.stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        (eventsView.get(event.getId()) == null || eventsView.isEmpty()) ? 0L
                                : eventsView.get(event.getId())))
                .toList();

        return result.stream()
                .map(comp -> {
                    List<Long> idsOfEventsForOneComp = comp.getEvents().stream().map(Event::getId).toList();
                    List<EventFullDto> toOneComp = eventFullDtos.stream()
                            .filter(eventFullDto -> idsOfEventsForOneComp.contains(eventFullDto.getId()))
                            .toList();

                    return CompilationMapper.toCompilationDto(comp, toOneComp);
                })
                .toList();
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId))
        );

        List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long>  confirmedRequests = getConfirmedRequestsForList(ids);
        Map<Long, Long> eventsView = statsService.getEventsView(compilation.getEvents());

        List<EventFullDto> eventFullDtos = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventsView.get(event.getId()) == null ? 0L : eventsView.get(event.getId())))
                .toList();

        return CompilationMapper.toCompilationDto(compilation, eventFullDtos);
    }

    private Map<Long, Long> getConfirmedRequestsForList(List<Long> ids) {
        return requestRepository.getConfirmedRequests(ids, Status.CONFIRMED).stream()
                .collect(Collectors.toMap(EventWithConfirmedRequests::getId, EventWithConfirmedRequests::getRequests));
    }

}
