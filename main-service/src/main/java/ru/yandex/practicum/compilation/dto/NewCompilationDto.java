package ru.yandex.practicum.compilation.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
public class NewCompilationDto {
    @UniqueElements
    private List<Long> events;

    private Boolean pinned = false;

    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Length(min = 1, max = 50)
    private String title;
}
