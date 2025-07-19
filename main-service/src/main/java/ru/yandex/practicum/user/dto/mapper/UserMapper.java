package ru.yandex.practicum.user.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.user.dto.UserDtoPost;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserShortDto;
import ru.yandex.practicum.user.model.User;

@UtilityClass
public class UserMapper {
    public User toUser(UserDtoPost userDtoPost) {
        return new User(-1L, userDtoPost.getName(), userDtoPost.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}