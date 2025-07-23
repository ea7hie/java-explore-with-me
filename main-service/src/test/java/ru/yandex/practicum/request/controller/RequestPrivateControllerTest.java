package ru.yandex.practicum.request.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.model.Status;
import ru.yandex.practicum.request.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestPrivateController.class)
public class RequestPrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private ParticipationRequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setStatus(Status.CONFIRMED);

        when(requestService.makeRequest(anyLong(), anyLong())).thenReturn(requestDto);
        when(requestService.getOwnRequests(anyLong())).thenReturn(List.of(requestDto));
        when(requestService.deleteRequest(anyLong(), anyLong())).thenReturn(requestDto);
    }

    @Test
    void addRequest_Success() throws Exception {
        mockMvc.perform(post("/users/100/requests")
                        .param("eventId", "50"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        verify(requestService).makeRequest(100L, 50L);
    }

    @Test
    void addRequest_MissingEventParam() throws Exception {
        mockMvc.perform(post("/users/10/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnRequests_Success() throws Exception {
        mockMvc.perform(get("/users/200/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
        verify(requestService).getOwnRequests(200L);
    }

    @Test
    void cancelRequest_Success() throws Exception {
        mockMvc.perform(patch("/users/300/requests/5/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        verify(requestService).deleteRequest(300L, 5L);
    }
}