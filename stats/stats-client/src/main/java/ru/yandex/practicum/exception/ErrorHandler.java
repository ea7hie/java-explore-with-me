package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Failed to convert value of type %s to required type %s",
                ex.getValue(),
                ex.getRequiredType().getSimpleName()
        );

        ErrorResponse response = new ErrorResponse(
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Method Argument Type Mismatch: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                "Unknown mistake.",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Unknown mistake. {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StatsClientException.class)
    public ResponseEntity<ErrorResponse> handleStatsClientException(StatsClientException ex) {
        ErrorResponse response = new ErrorResponse(
                "Error in stats-server.",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("StatsClientException error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
