package ru.yandex.practicum.exception.model;

public class IsNotUniqueEmailException extends RuntimeException {
    public IsNotUniqueEmailException(String message) {
        super(message);
    }
}
