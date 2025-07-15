package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.dao.StatisticsRepository;
import ru.yandex.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private StatisticsRepository repository;

    @InjectMocks
    private StatisticsServiceImpl service;

    private StatisticDtoPost dtoPost;
    private Statistic savedStatistic;

    @BeforeEach
    void setup() {
        dtoPost = new StatisticDtoPost();
        dtoPost.setApp("TestApp");
        dtoPost.setUri("/test");
        dtoPost.setIp("192.168.0.1");
        dtoPost.setTimestamp(LocalDateTime.now());

        savedStatistic = new Statistic(1L, "TestApp", "/test", "192.168.0.1", dtoPost.getTimestamp());
    }

    @Test
    void getStats_WithEmptyUris_ShouldCallFindHitsByTimestampBetween() {
        String start = "2023-01-01 00:00:00";
        String end = "2023-01-02 00:00:00";
        List<String> uris = List.of();
        Boolean unique = true;

        List<StatisticDtoGet> mockStats = List.of(new StatisticDtoGet("App", "/uri", 5L));
        when(repository.findHitsByTimestampBetween(any(LocalDateTime.class), any(LocalDateTime.class), eq(unique)))
                .thenReturn(mockStats);

        List<StatisticDtoGet> result = service.getStats(start, end, uris, unique);

        assertThat(result).hasSize(1);
        verify(repository, times(1)).findHitsByTimestampBetween(any(LocalDateTime.class),
                any(LocalDateTime.class), eq(unique));
        verify(repository, never()).findHitsByUriInAndTimestampBetween(anyList(),
                any(LocalDateTime.class), any(LocalDateTime.class), anyBoolean());
    }

    @Test
    void getStats_WithNonEmptyUris_ShouldCallFindHitsByUriInAndTimestampBetween() {
        String start = "2023-01-01 00:00:00";
        String end = "2023-01-02 00:00:00";
        List<String> uris = List.of("/uri1", "/uri2");
        Boolean unique = false;

        List<StatisticDtoGet> mockStats = List.of(new StatisticDtoGet("App", "/uri1", 10L));
        when(repository.findHitsByUriInAndTimestampBetween(eq(uris),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(unique)))
                .thenReturn(mockStats);

        List<StatisticDtoGet> result = service.getStats(start, end, uris, unique);

        assertThat(result).hasSize(1);
        verify(repository, times(1)).findHitsByUriInAndTimestampBetween(eq(uris),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(unique));
        verify(repository, never()).findHitsByTimestampBetween(any(LocalDateTime.class),
                any(LocalDateTime.class), anyBoolean());
    }
}