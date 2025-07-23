package ru.yandex.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.comment.dao.CommentRepository;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.dto.mapper.CommentMapper;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.comment.model.CommentState;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;
import ru.yandex.practicum.event.service.StatsService;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.OperationNotAllowedException;
import ru.yandex.practicum.request.dao.RequestRepository;
import ru.yandex.practicum.request.model.Status;
import ru.yandex.practicum.user.dao.UserRepository;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsService statsService;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("User with id={} created comment={} to event with id={}", userId, newCommentDto, eventId);

        User user = getUserByIdOrThrow(userId);
        Event event = getPublishedEventByIdOrThrow(eventId);
        Map<Long, Long> eventsView = statsService.getEventsView(List.of(event));
        long views = (eventsView.get(event.getId()) == null || eventsView.isEmpty()
                || eventsView.get(event.getId()) == 0L) ? 0L : eventsView.get(event.getId());
        long confirmedRequests = getConfirmedRequests(event);

        Comment saved = commentRepository.save(CommentMapper.toComment(newCommentDto, user, event));
        return CommentMapper.toCommentDto(saved, confirmedRequests, views);
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        log.info("get comments to event with id={}", eventId);

        Event event = getPublishedEventByIdOrThrow(eventId);
        List<Comment> comments = commentRepository.findByEvent(event, PageRequest.of(from, size));
        Map<Long, Long> eventsView = statsService.getEventsView(List.of(event));
        long views = (eventsView.get(event.getId()) == null || eventsView.isEmpty()
                || eventsView.get(event.getId()) == 0L) ? 0L : eventsView.get(event.getId());
        long confirmedRequests = getConfirmedRequests(event);

        return comments.stream()
                .map(comment -> CommentMapper.toCommentDto(comment, confirmedRequests, views))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("get comment with id={}", commentId);

        Comment comment = getCommentOrThrow(commentId);
        Map<Long, Long> eventsView = statsService.getEventsView(List.of(comment.getEvent()));
        long views = (eventsView.get(comment.getEvent().getId()) == null || eventsView.isEmpty()
                || eventsView.get(comment.getEvent().getId()) == 0L) ? 0L : eventsView.get(comment.getEvent().getId());
        long confirmedRequests = getConfirmedRequests(comment.getEvent());

        return CommentMapper.toCommentDto(comment, confirmedRequests, views);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        log.info("user with id={} want to update comment with id={} on={}", userId, commentId, newCommentDto);

        User user = getUserByIdOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);

        isUserAuthor(userId, comment);
        isNotPublished(comment);

        comment.setText(newCommentDto.getText());
        comment.setState(CommentState.PENDING);

        Map<Long, Long> eventsView = statsService.getEventsView(List.of(comment.getEvent()));
        long views = (eventsView.get(comment.getEvent().getId()) == null || eventsView.isEmpty()
                || eventsView.get(comment.getEvent().getId()) == 0L) ? 0L : eventsView.get(comment.getEvent().getId());
        long confirmedRequests = getConfirmedRequests(comment.getEvent());

        comment.setUpdatedOn(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment, confirmedRequests, views);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("user with id={} want to delete comment with id={}", userId, commentId);

        getUserByIdOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);

        isUserAuthor(userId, comment);
        isNotPublished(comment);

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateCommentStatusByAdmin(Long commentId, boolean isConfirm) {
        log.info("Confirm/reject comment with id={} to={}", commentId, isConfirm);
        Comment comment = getCommentOrThrow(commentId);

        if (isConfirm) {
            comment.setState(CommentState.CONFIRMED);
        } else {
            comment.setState(CommentState.REJECTED);
        }

        comment.setPublishedOn(LocalDateTime.now());

        Map<Long, Long> eventsView = statsService.getEventsView(List.of(comment.getEvent()));
        long views = (eventsView.get(comment.getEvent().getId()) == null || eventsView.isEmpty()
                || eventsView.get(comment.getEvent().getId()) == 0L) ? 0L : eventsView.get(comment.getEvent().getId());
        long confirmedRequests = getConfirmedRequests(comment.getEvent());
        return CommentMapper.toCommentDto(commentRepository.save(comment), confirmedRequests, views);
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
    }

    private Event getPublishedEventByIdOrThrow(Long eventId) {
        return eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));
    }

    private void isUserAuthor(Long userId, Comment comment) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new OperationNotAllowedException("User with id " + userId + " is not an author.");
        }
    }

    private void isNotPublished(Comment comment) {
        if (comment.getState() == CommentState.CONFIRMED) {
            throw new OperationNotAllowedException("Comment have been published already.");
        }
    }

    private long getConfirmedRequests(Event event) {
        return requestRepository.getConfirmedRequests(event.getId(), Status.CONFIRMED);
    }
}
