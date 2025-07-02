package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.dto.ExceptionDto;
import ru.yandex.practicum.exception.model.AccessError;
import ru.yandex.practicum.exception.model.IsNotUniqueEmailException;
import ru.yandex.practicum.exception.model.NotFoundException;
import ru.yandex.practicum.exception.model.ValidationException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleNotFoundExc(final NotFoundException e) {
        return new ExceptionDto("Данные согласно вашему запросу не найдены", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleValidationExc(final ValidationException e) {
        return new ExceptionDto("Ошибка валидации данных, проверьте корректность введённых данных.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleValidationExc(final MethodArgumentNotValidException e) {
        return new ExceptionDto("Ошибка валидации данных, проверьте корректность введённых данных.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleIsNotUniqueEmailException(final IsNotUniqueEmailException e) {
        return new ExceptionDto("Данный email уже зарегистрирован в системе.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAccessError(final AccessError e) {
        return new ExceptionDto("Ошибка при попытке доступа.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleSomeExc(final Throwable e) {
        return new ExceptionDto("Сбой в работе сервера.", e.getMessage());
    }
}