package ru.yandex.practicum.event.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.event.model.StateActionForAdmin;
import ru.yandex.practicum.location.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future

    private  LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionForAdmin stateAction;

    @Length(min = 3, max = 120)
    private String title;
}
