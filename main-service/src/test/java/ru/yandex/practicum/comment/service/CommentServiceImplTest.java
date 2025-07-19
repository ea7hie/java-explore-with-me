package ru.yandex.practicum.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.yandex.practicum.comment.dao.CommentRepository;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private Event event;
    private User user;
    private NewCommentDto newCommentDtoMock;
    private CommentDto commentDtoMock;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        event = new Event();
        event.setId(2L);

        comment = new Comment();
        comment.setId(3L);
        comment.setAuthor(user);
        comment.setEvent(event);

        newCommentDtoMock = new NewCommentDto();
        newCommentDtoMock.setText("Test");

        commentDtoMock = new CommentDto();
    }

    @Test
    void createComment_UserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentService.createComment(99L, event.getId(), newCommentDtoMock));
    }

    @Test
    void updateComment_AlreadyPublished() {
        comment.setState(CommentState.CONFIRMED);

        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(OperationNotAllowedException.class,
                () -> commentService.updateComment(user.getId(), comment.getId(), newCommentDtoMock));
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        commentService.deleteComment(user.getId(), comment.getId());

        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    void getCommentById_NotFound() {
        when(commentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentService.getCommentById(999L));
    }
}