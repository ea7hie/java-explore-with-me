package ru.yandex.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticDtoGet {
    private String app;
    private String uri;
    private Long hits;
}
