package ru.yandex.practicum.event.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.event.location.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    LocationDto location;

    @NotNull
    Boolean paid = false;

    @NotNull
    Integer participantLimit = 0;

    @NotNull
    Boolean requestModeration = true;

    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}