package ru.yandex.practicum.comment.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.event.dto.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.dto.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;


@UtilityClass
public class CommentMapper {
    public Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        Comment comment = new Comment();
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setText(newCommentDto.getText());
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment, long confirmedRequests, long views) {
        return new CommentDto(
                comment.getId(),
                EventMapper.toEventShortDto(comment.getEvent(), confirmedRequests, views),
                UserMapper.toUserShortDto(comment.getAuthor()),
                comment.getText(),
                comment.getState(),
                comment.getCreatedOn(),
                comment.getUpdatedOn() != null ? comment.getUpdatedOn() : null,
                comment.getPublishedOn() != null ? comment.getPublishedOn() : null
        );
    }
}
