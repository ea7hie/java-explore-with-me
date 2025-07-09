package ru.yandex.practicum.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StatisticsRepositoryTest {

    @Autowired
    private StatisticsRepository repository;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        repository.save(new Statistic(null, "App1", "/home", "192.168.1.1", now.minusHours(1)));
        repository.save(new Statistic(null, "App1", "/home", "192.168.1.2", now.minusMinutes(30)));
        repository.save(new Statistic(null, "App1", "/home", "192.168.1.1", now.minusMinutes(10))); // duplicate IP
        repository.save(new Statistic(null, "App2", "/login", "10.0.0.1", now.minusMinutes(5)));
    }

    @Test
    void testFindHitsByUriInAndTimestampBetween_UniqueFalse() {
        List<StatisticDtoGet> result = repository.findHitsByUriInAndTimestampBetween(
                List.of("/home", "/login"),
                now.minusHours(2),
                now,
                false
        );

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(dto ->
                dto.getUri().equals("/home") && dto.getHits() == 3);
        assertThat(result).anyMatch(dto ->
                dto.getUri().equals("/login") && dto.getHits() == 1);
    }

    @Test
    void testFindHitsByUriInAndTimestampBetween_UniqueTrue() {
        List<StatisticDtoGet> result = repository.findHitsByUriInAndTimestampBetween(
                List.of("/home", "/login"),
                now.minusHours(2),
                now,
                true
        );

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(dto ->
                dto.getUri().equals("/home") && dto.getHits() == 2); // уникальных IP: 192.168.1.1, 192.168.1.2
    }

    @Test
    void testFindHitsByTimestampBetween_UniqueFalse() {
        List<StatisticDtoGet> result = repository.findHitsByTimestampBetween(
                now.minusHours(2),
                now,
                false
        );

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(dto ->
                dto.getApp().equals("App1") && dto.getHits() == 3);
        assertThat(result).anyMatch(dto ->
                dto.getApp().equals("App2") && dto.getHits() == 1);
    }

    @Test
    void testFindHitsByTimestampBetween_UniqueTrue() {
        List<StatisticDtoGet> result = repository.findHitsByTimestampBetween(
                now.minusHours(2),
                now,
                true
        );

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(dto ->
                dto.getApp().equals("App1") && dto.getHits() == 2); // уникальные IP
    }
}