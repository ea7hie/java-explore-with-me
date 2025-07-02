package ru.yandex.practicum.model.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.model.Statistic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsMapperTest {

    @Test
    void testToStatisticDtoGet() {
        Statistic stat = new Statistic(
                42L,
                "TestApp",
                "/test",
                "192.168.0.1",
                LocalDateTime.now()
        );

        StatisticDtoGet dto = StatisticsMapper.toStatisticDtoGet(stat, 10L);

        assertEquals("TestApp", dto.getApp());
        assertEquals("/test", dto.getUri());
        assertEquals(10L, dto.getHits());
    }

    @Test
    void testToStatistic() {
        LocalDateTime time = LocalDateTime.now();

        StatisticDtoPost dtoPost = new StatisticDtoPost();
        dtoPost.setApp("MapperApp");
        dtoPost.setUri("/map");
        dtoPost.setIp("10.0.0.1");
        dtoPost.setTimestamp(time);

        Statistic stat = StatisticsMapper.toStatistic(dtoPost);

        assertEquals(-1L, stat.getId());
        assertEquals("MapperApp", stat.getApp());
        assertEquals("/map", stat.getUri());
        assertEquals("10.0.0.1", stat.getIp());
        assertEquals(time, stat.getTimestamp());
    }
}