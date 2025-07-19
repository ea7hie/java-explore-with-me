package ru.yandex.practicum.comment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.service.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentPublicController.class)
public class CommentPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private CommentDto commentDtoMock;

    @BeforeEach
    void setup() {
        commentDtoMock = new CommentDto();
        commentDtoMock.setId(1L);
        commentDtoMock.setText("Test comment");
    }

    @Test
    void getEventComments_Success() throws Exception {
        Long eventId = 1L;
        int from = 0, size = 10;
        List<CommentDto> expectedList = List.of(commentDtoMock);

        when(commentService.getEventComments(eq(eventId), eq(from), eq(size)))
                .thenReturn(expectedList);

        mockMvc.perform(get("/events/{eventId}/comments", eventId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getEventComments_DefaultPagination() throws Exception {
        Long eventId = 2L;
        List<CommentDto> expectedList = List.of();

        when(commentService.getEventComments(eq(eventId), eq(0), eq(10)))
                .thenReturn(expectedList);

        mockMvc.perform(get("/events/{eventId}/comments", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCommentById_Success() throws Exception {
        Long commentId = 4L;
        when(commentService.getCommentById(eq(commentId)))
                .thenReturn(commentDtoMock);

        mockMvc.perform(get("/comment/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test comment"));
    }
}