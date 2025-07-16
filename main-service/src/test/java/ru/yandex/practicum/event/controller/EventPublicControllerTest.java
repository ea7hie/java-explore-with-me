package ru.yandex.practicum.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.model.Sort;
import ru.yandex.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPublicController.class)
class EventPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void filteredRequest() throws Exception {
        List<Long> categories = List.of(1L, 2L);
        String text = "марафон";
        Boolean paid = true;
        String rangeStart = "2023-05-01 10:00:00";
        String rangeEnd = "2023-05-30 23:59:59";
        boolean onlyAvailable = true;
        String sortParam = "VIEWS";
        int from = 10, size = 5;

        List<EventShortDto> events = List.of(new EventShortDto());
        when(eventService.findEventsByText(
                anyString(),
                eq(categories),
                eq(paid),
                argThat(start -> start.isEqual(LocalDateTime.parse(rangeStart, formatter))),
                argThat(end -> end.isEqual(LocalDateTime.parse(rangeEnd, formatter))),
                eq(onlyAvailable),
                eq(Sort.VIEWS),
                eq(from),
                eq(size),
                any(),
                any()
        ))
                .thenReturn(events);

        mockMvc.perform(get("/events")
                        .param("text", text)
                        .param("categories", "1", "2")
                        .param("paid", "true")
                        .param("rangeStart", rangeStart)
                        .param("rangeEnd", rangeEnd)
                        .param("onlyAvailable", "true")
                        .param("sort", sortParam)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldValidateNegativeFrom() throws Exception {
        mockMvc.perform(get("/events")
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidDateFormatTest() throws Exception {
        mockMvc.perform(get("/events")
                        .param("rangeStart", "2023-13-32 00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEventWithNegativeId() throws Exception {
        mockMvc.perform(get("/events/-5"))
                .andExpect(status().isBadRequest());
    }
}
