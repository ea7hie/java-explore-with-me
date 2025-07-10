package ru.yandex.practicum.user.dto.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserDtoPost;
import ru.yandex.practicum.user.dto.UserShortDto;
import ru.yandex.practicum.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    @Test
    void toUser_correctMapping() {
        UserDtoPost dtoPost = new UserDtoPost("Ivan Petrov", "ivan@example.com");
        User user = UserMapper.toUser(dtoPost);

        assertThat(user.getId()).isEqualTo(-1L);
        assertThat(user.getName()).isEqualTo(dtoPost.getName());
        assertThat(user.getEmail()).isEqualTo(dtoPost.getEmail());
    }

    @Test
    void toUserDto_correctMapping() {
        User user = new User(123L, "John Smith", "john@example.com");
        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void toUserShortDto_correctMapping() {
        User user = new User(456L, "Maria Ivanova", "maria@example.com");
        UserShortDto dto = UserMapper.toUserShortDto(user);

        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
    }
}