package ru.yandex.practicum.event.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.event.dto.get.EventFullDto;
import ru.yandex.practicum.event.dto.get.EventShortDto;
import ru.yandex.practicum.event.dto.in.NewEventDto;
import ru.yandex.practicum.event.dto.in.UpdateEventUserRequest;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.model.Status;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPrivateController.class)
public class EventPrivateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void createEvent_InvalidUserId() throws Exception {
        mockMvc.perform(post("/users/-1/events"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск событий с пагинацией should return OK with pagination")
    void getEvents_WithPagination() throws Exception {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        List<EventShortDto> expected = List.of(eventShortDto);
        when(eventService.findEventsByInitiatorId(eq(2L), eq(0), eq(10))).thenReturn(expected);

        mockMvc.perform(get("/users/2/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getEventById_Success() throws Exception {
        EventFullDto createdEvent = new EventFullDto();
        createdEvent.setId(2L);
        when(eventService.findEventByInitiatorIdAndEventId(1L, 2L))
                .thenReturn(createdEvent);

        mockMvc.perform(get("/users/1/events/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void updateEvent_Success() throws Exception {
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setTitle("New Title");

        EventFullDto updated = new EventFullDto();
        updated.setTitle("New Title");
        when(eventService.updateEventsByInitiatorIdAndEventId(1L, 2L, request)).thenReturn(updated);

        mockMvc.perform(patch("/users/1/events/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void getRequestsForEvent() throws Exception {
        ParticipationRequestDto request = new ParticipationRequestDto();
        request.setId(1L);
        List<ParticipationRequestDto> list = Collections.singletonList(request);

        when(eventService.getListRequestsToEvent(3L, 4L)).thenReturn(list);

        mockMvc.perform(get("/users/3/events/4/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateRequestStatus_Success() throws Exception {
        EventRequestStatusUpdateRequest dto = new EventRequestStatusUpdateRequest();
        dto.setStatus(Status.CONFIRMED);
        dto.setRequestIds(List.of(1L));

        when(eventService.changeStatusRequestToEvent(anyLong(), anyLong(), any()))
                .thenReturn(new EventRequestStatusUpdateResult());

        mockMvc.perform(patch("/users/5/events/6/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateEvent_InvalidUserId() throws Exception {
        mockMvc.perform(patch("/users/-1/events/2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_InvalidDto() throws Exception {
        NewEventDto invalidDto = new NewEventDto();
        mockMvc.perform(post("/users/3/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
