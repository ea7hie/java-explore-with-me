package ru.yandex.practicum.comment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
public class CommentAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private CommentDto mockedCommentDto;

    @BeforeEach
    void setup() {
        mockedCommentDto = new CommentDto();
    }

    @Test
    void testUpdateCommentStatus_Success() throws Exception {
        Long commentId = 1L;
        boolean isConfirm = true;
        when(commentService.updateCommentStatusByAdmin(eq(commentId), eq(isConfirm)))
                .thenReturn(mockedCommentDto);

        mockMvc.perform(patch("/admin/comment/{commentId}", commentId)
                        .param("isConfirm", String.valueOf(isConfirm)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateCommentStatus_InvalidId() throws Exception {
        Long commentId = -1L;
        boolean isConfirm = false;

        mockMvc.perform(patch("/admin/comment/{commentId}", commentId)
                        .param("isConfirm", String.valueOf(isConfirm)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testUpdateCommentStatus_NotFound() throws Exception {
        Long commentId = 999L;
        boolean isConfirm = true;
        doThrow(new NotFoundException("Comment not found"))
                .when(commentService).updateCommentStatusByAdmin(anyLong(), anyBoolean());

        mockMvc.perform(patch("/admin/comment/{commentId}", commentId)
                        .param("isConfirm", String.valueOf(isConfirm)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMissingIsConfirmParameter() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(patch("/admin/comment/{commentId}", commentId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MissingServletRequestParameterException.class, result.getResolvedException()));
    }
}