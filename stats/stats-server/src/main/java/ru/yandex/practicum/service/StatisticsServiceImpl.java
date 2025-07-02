package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.dao.StatisticsRepository;
import ru.yandex.practicum.model.Statistic;
import ru.yandex.practicum.model.mapper.StatisticsMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;

    @Override
    public List<Statistic> getAll() {
        return statisticsRepository.findAll();
    }

    @Override
    public List<StatisticDtoGet> getAllDTo() {
        return statisticsRepository.findAll().stream().map(st -> StatisticsMapper.toStatisticDtoGet(st, 0L)).toList();
    }

    @Override
    public StatisticDtoGet saveNewHit(StatisticDtoPost statisticDtoPost) {
        Statistic save = statisticsRepository.save(StatisticsMapper.toStatistic(statisticDtoPost));
        return StatisticsMapper.toStatisticDtoGet(save, 0L);
    }

    @Override
    public List<StatisticDtoGet> getStats(String start, String end, List<String> uris, Boolean unique) {
        // return statisticsRepository.findHitsByUriInAndTimestampBetween();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<StatisticDtoGet> statistics;

        LocalDateTime dtStart = LocalDateTime.parse(start, formatter);
        LocalDateTime dtEnd = LocalDateTime.parse(end, formatter);

        if (uris.isEmpty()) {
            statistics = statisticsRepository.findHitsByTimestampBetween(
                    dtStart,
                    dtEnd,
                    unique
            );
            log.info("Retrieved {} statistics for all uris from {} to {}, unique IPs: {}", statistics.size(), start, end, unique);

        } else {
            statistics = statisticsRepository.findHitsByUriInAndTimestampBetween(
                    uris,
                    dtStart,
                    dtEnd,
                    unique
            );
            log.info("Retrieved {} statistics for uris: {} from {} to {}, unique IPs: {}", statistics.size(), uris, start, end, unique);
        }

        return statistics;
    }
}
