package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.StatsClient;
import ru.yandex.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class StatsService {
    private final StatsClient statClient;

    public Map<Long, Long> getEventsView(List<Event> events) {
        //eventId, views
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .toList();

        if (publishedEvents.isEmpty()) {
            return views;
        }

        Optional<LocalDateTime> minPublishedOn = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minPublishedOn.isEmpty()) {
            return views;
        }

        LocalDateTime start = minPublishedOn.get();
        List<String> uri = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .toList();

        List<StatisticDtoGet> stats = statClient.getStats(start, LocalDateTime.now(), uri, false);

        stats.forEach(statDto -> {
            String[] parts = statDto.getUri().split("/");
            if (parts.length >= 2) {
                Long eventId = Long.parseLong(parts[2]);
                views.put(eventId, (statDto.getHits() == null) ? 0L : statDto.getHits());
            }
        });

        return views;
    }

    public void sendHit(String uri, String ip) {
        StatisticDtoPost statisticDtoPost = new StatisticDtoPost("main-service", uri, ip, LocalDateTime.now());
        statClient.hit(statisticDtoPost);
    }
}
