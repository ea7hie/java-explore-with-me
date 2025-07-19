package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.dto.UserDtoPost;
import ru.yandex.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDtoPost userDtoPost);

    List<UserDto> getAll(List<Long> ids, int from, int size);

    void delete(Long id);
}
