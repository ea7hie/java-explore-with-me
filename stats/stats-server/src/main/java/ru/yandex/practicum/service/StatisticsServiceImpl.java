package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.dao.StatisticsRepository;
import ru.yandex.practicum.exception.StatsClientException;
import ru.yandex.practicum.model.mapper.StatisticsMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveNewHit(StatisticDtoPost statisticDtoPost) {
        statisticsRepository.save(StatisticsMapper.toStatistic(statisticDtoPost));
    }

    @Override
    public List<StatisticDtoGet> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime dtStart = LocalDateTime.parse(start, formatter).minusSeconds(2);
        LocalDateTime dtEnd = LocalDateTime.parse(end, formatter);
        List<StatisticDtoGet> statistics;

        if (dtStart.isAfter(dtEnd)) {
            throw new StatsClientException("End cannot be earlier then start.");
        }
        if (uris.isEmpty() || uris.contains("/events")) {
            statistics = statisticsRepository.findHitsByTimestampBetween(
                    dtStart,
                    dtEnd,
                    unique
            );
            log.info("Retrieved {} statistics for all uris from {} to {}, unique IPs: {}",
                    statistics.size(), start, end, unique);
        } else {
            statistics = statisticsRepository.findHitsByUriInAndTimestampBetween(
                    uris,
                    dtStart,
                    dtEnd,
                    unique
            );
            log.info("Retrieved {} statistics for uris: {} from {} to {}, unique IPs: {}",
                    statistics.size(), uris, start, end, unique);
        }

        return statistics;
    }
}
