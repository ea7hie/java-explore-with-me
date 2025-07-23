package ru.yandex.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/comment")
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentStatusByAdmin(@PathVariable @Positive Long commentId,
                                                 @RequestParam boolean isConfirm) {
        return commentService.updateCommentStatusByAdmin(commentId, isConfirm);
    }
}
