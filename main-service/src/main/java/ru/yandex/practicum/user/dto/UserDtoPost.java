package ru.yandex.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoPost {
    @NotBlank
    @Length(min = 2, max = 250)
    String name;

    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    String email;
}
