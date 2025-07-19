package ru.yandex.practicum.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.user.dao.UserRepository;
import ru.yandex.practicum.user.dto.UserDtoPost;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testAdd_DuplicateEmail_ThrowException() {
        UserDtoPost userDtoPost = new UserDtoPost("Jane Doe", "jane@example.com");
        when(userRepository.existsByEmail(userDtoPost.getEmail())).thenReturn(true);

        assertThrows(DuplicateException.class, () -> {
            userService.add(userDtoPost);
        });

        verify(userRepository, never()).save(any());
    }
}