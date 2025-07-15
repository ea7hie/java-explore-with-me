package ru.yandex.practicum.exception;

public class StatsClientException extends RuntimeException {
    public StatsClientException(String message) {
        super(message);
    }

    public StatsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
