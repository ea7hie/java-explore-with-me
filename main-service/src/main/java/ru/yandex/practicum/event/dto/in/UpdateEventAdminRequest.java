package ru.yandex.practicum.event.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.location.LocationDto;
import ru.yandex.practicum.event.model.StateActionForAdmin;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;

    Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionForAdmin stateAction;

    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}
