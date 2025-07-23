package ru.yandex.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable @Positive Long eventId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/comment/{commentId}")
    public CommentDto getCommentById(@PathVariable @Positive Long commentId) {
        return commentService.getCommentById(commentId);
    }
}
