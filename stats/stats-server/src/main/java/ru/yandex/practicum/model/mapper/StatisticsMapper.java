package ru.yandex.practicum.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.model.Statistic;

@UtilityClass
public class StatisticsMapper {
    public StatisticDtoGet toStatisticDtoGet(Statistic statistic, Long hits) {
        return new StatisticDtoGet(
                statistic.getApp(),
                statistic.getUri(),
                hits
        );
    }

    public Statistic toStatistic(StatisticDtoPost statisticDtoPost) {
        return new Statistic(-1L,
                statisticDtoPost.getApp(),
                statisticDtoPost.getUri(),
                statisticDtoPost.getIp(),
                statisticDtoPost.getTimestamp()
        );
    }
}
