package ru.yandex.practicum.event.dto.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.event.location.LocationDto;
import ru.yandex.practicum.event.model.StateActionForUser;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;

    Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    String description;

    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionForUser stateAction;

    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}
