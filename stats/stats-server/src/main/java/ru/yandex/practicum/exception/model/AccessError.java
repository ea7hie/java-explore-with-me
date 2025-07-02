package ru.yandex.practicum.exception.model;

public class AccessError extends RuntimeException {
    public AccessError(String message) {
        super(message);
    }
}
