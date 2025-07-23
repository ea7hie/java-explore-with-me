package ru.yandex.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NewCommentDto {
    @NotBlank
    @Size(min = 3, max = 512)
    private String text;
}
