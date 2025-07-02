package ru.yandex.practicum.service;

import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;

import java.util.List;

public interface StatisticsService {
    public StatisticDtoGet saveNewHit(StatisticDtoPost statisticDtoPost);

    public List<StatisticDtoGet> getStats(String start, String end, List<String> uris, Boolean unique);
}

