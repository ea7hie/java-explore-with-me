package ru.yandex.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.service.StatisticsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetStatsReturnsList() throws Exception {
        String start = "2024-01-01 00:00:00";
        String end = "2024-12-31 23:59:59";

        List<StatisticDtoGet> stats = List.of(
                new StatisticDtoGet("service", "/path", 10L)
        );

        Mockito.when(statisticsService.getStats(eq(start), eq(end), anyList(), eq(false)))
                .thenReturn(stats);

        mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "/path")
                        .param("unique", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].app").value("service"))
                .andExpect(jsonPath("$[0].uri").value("/path"))
                .andExpect(jsonPath("$[0].hits").value(10));
    }

    @Test
    void testGetStatsWithEmptyUrisAndDefaultUnique() throws Exception {
        String start = "2024-01-01 00:00:00";
        String end = "2024-12-31 23:59:59";

        Mockito.when(statisticsService.getStats(eq(start), eq(end), eq(List.of()), eq(false)))
                .thenReturn(List.of());

        mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}