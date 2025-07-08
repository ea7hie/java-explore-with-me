package ru.yandex.practicum.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.StatisticDtoPost;
import ru.yandex.practicum.service.StatisticsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDtoGet record(@RequestBody StatisticDtoPost statisticDtoPost) {
        return statisticsService.saveNewHit(statisticDtoPost);
    }

    @GetMapping("/stats")
    public List<StatisticDtoGet> getStatistic(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        log.info("GET /stats - Getting statistic for uris: {} from: {} to {}, unique: {}", uris, start, end, unique);
        return statisticsService.getStats(start, end, uris, unique);
    }
}
