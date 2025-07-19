package ru.yandex.practicum.comment.service;

import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    List<CommentDto> getEventComments(Long eventId, int from, int size);

    CommentDto getCommentById(Long commentId);

    CommentDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    void deleteComment(Long userId, Long commentId);

    CommentDto updateCommentStatusByAdmin(Long commentId, boolean isConfirm);
}
