package ru.yandex.practicum.event.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.category.model.Category;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    @Length(max = 2000)
    @NotBlank
    String annotation;

    @Column
    @NotNull
    Category category;

    @Column
    @NotNull
    LocalDateTime createdOn;

    @Column
    @Length(max = 7000)
    @NotBlank
    String description;

    @Column
    @Length(max = 170)
    @NotBlank
    String title;
}
