package ru.yandex.practicum.comment.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentPrivateController.class)
public class CommentPrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private NewCommentDto newCommentDtoMock;
    private CommentDto commentDtoMock;

    @BeforeEach
    void setup() {
        newCommentDtoMock = new NewCommentDto();
        newCommentDtoMock.setText("Test comment text");
        commentDtoMock = new CommentDto();
        commentDtoMock.setId(1L);
    }

    @Test
    void createComment_Success() throws Exception {
        final long userId = 1L;
        final long eventId = 2L;
        when(commentService.createComment(anyLong(), anyLong(), eq(newCommentDtoMock)))
                .thenReturn(commentDtoMock);

        mockMvc.perform(post("/user/{userId}/events/{eventId}/comment", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newCommentDtoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateComment_Success() throws Exception {
        final long userId = 1L;
        final long commentId = 3L;
        when(commentService.updateComment(userId, commentId, newCommentDtoMock))
                .thenReturn(commentDtoMock);

        mockMvc.perform(patch("/user/{userId}/comment/{commentId}", userId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newCommentDtoMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateComment_CommentNotFound() {
        final long userId = 10L;
        final long commentId = 999L;
        doThrow(new NotFoundException("Comment not found"))
                .when(commentService).updateComment(userId, commentId, newCommentDtoMock);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(patch("/user/{userId}/comment/{commentId}", userId, commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(newCommentDtoMock)))
                    .andExpect(status().isNotFound());
        });
    }

    @Test
    void deleteComment_Success() throws Exception {
        final long userId = 1L;
        final long commentId = 4L;

        mockMvc.perform(delete("/user/{userId}/comment/{commentId}", userId, commentId))
                .andExpect(status().isNoContent());

    }

    @Test
    void deleteComment_NotFound() {
        final long userId = 5L;
        final long commentId = 888L;
        doThrow(new NotFoundException("Comment not found"))
                .when(commentService).deleteComment(userId, commentId);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(delete("/user/{userId}/comment/{commentId}", userId, commentId))
                    .andExpect(status().isNotFound());
        });
    }

    private static String asJsonString(final Object obj) {
        try {
            final var mapper = new MappingJackson2HttpMessageConverter()
                    .getObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}