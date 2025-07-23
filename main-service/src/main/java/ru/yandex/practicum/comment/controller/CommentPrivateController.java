package ru.yandex.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user/{userId}")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comment/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.updateComment(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}
