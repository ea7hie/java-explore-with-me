package ru.yandex.practicum.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.service.EventService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventAdminController.class)
public class EventAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void shouldReturnAllEvents() throws Exception {
        List<EventFullDto> expected = Collections.singletonList(createTestEventFullDto());
        when(eventService.findEvents(any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(expected);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private EventFullDto createTestEventFullDto() {
        EventFullDto dto = new EventFullDto();
        dto.setId(1L);
        dto.setTitle("Test Event");
        return dto;
    }
}
