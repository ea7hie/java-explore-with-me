package ru.yandex.practicum.service;

import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;

import java.util.List;

public interface StatisticsService {
    void saveNewHit(StatisticDtoPost statisticDtoPost);

    List<StatisticDtoGet> getStats(String start, String end, List<String> uris, Boolean unique);
}

