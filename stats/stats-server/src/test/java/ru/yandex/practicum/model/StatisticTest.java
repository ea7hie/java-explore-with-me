package ru.yandex.practicum.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.practicum.dao.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StatisticTest {

    @Autowired
    private StatisticsRepository repository;

    @Test
    void testSaveAndLoadStatistic() {
        Statistic stat = new Statistic();
        stat.setApp("TestApp");
        stat.setUri("/test");
        stat.setIp("127.0.0.1");
        stat.setTimestamp(LocalDateTime.now());

        Statistic saved = repository.save(stat);

        Optional<Statistic> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getApp()).isEqualTo("TestApp");
        assertThat(found.get().getUri()).isEqualTo("/test");
        assertThat(found.get().getIp()).isEqualTo("127.0.0.1");
        assertThat(found.get().getTimestamp()).isEqualTo(stat.getTimestamp());
    }

    @Test
    void testGeneratedId() {
        Statistic stat = new Statistic(null, "AppX", "/path", "10.0.0.1", LocalDateTime.now());
        Statistic saved = repository.save(stat);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }
}