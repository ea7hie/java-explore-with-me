package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.dao.UserRepository;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        List<User> users = (ids == null || ids.isEmpty()) ? userRepository.findAll()
                : userRepository.findAllByIdIn(ids);

        return users.stream()
                .skip(from)
                .limit(size)
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new DuplicateException("Email already exists: " + newUserRequest.getEmail());
        }
        User user = userRepository.save(UserMapper.toUser(newUserRequest));
        log.info("User was created: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User with id={}, was deleted", id);
        }

        throw new NotFoundException(String.format("User with id=%d was not found", id));
    }
}


