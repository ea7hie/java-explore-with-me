package ru.yandex.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String reason;
    private String message;
    private HttpStatus status;
    private String timestamp;
}