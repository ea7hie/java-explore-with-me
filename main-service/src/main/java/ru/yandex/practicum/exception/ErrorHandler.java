package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                "The required object was not found.",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Not found error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateException ex) {
        ErrorResponse response = new ErrorResponse(
                "Integrity constraint has been violated.",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Duplicate error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);

        String message = String.format("Field: %s. Error: %s. Value: %s",
                firstError.getField(),
                firstError.getDefaultMessage(),
                firstError.getRejectedValue());

        ErrorResponse response = new ErrorResponse(
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Method Argument Not Valid: {}", message);
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        ErrorResponse response = new ErrorResponse(
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Category is not empty: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleOperationNotAllowed(OperationNotAllowedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "For the requested operation the conditions are not met",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(dateTimeFormatter)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeException(DateTimeException ex) {
        ErrorResponse response = new ErrorResponse(
                "Wrong value of the input date",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("DateTimeException error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleDateTimeException(Throwable ex) {
        ErrorResponse response = new ErrorResponse(
                "Exception in service.",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(dateTimeFormatter)
        );

        log.error("Exception in service: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
