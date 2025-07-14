package ru.yandex.practicum.event.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.location.Location;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Length(min = 20, max = 2000)
    @NotBlank
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column
    private LocalDateTime createdOn;

    @Column
    @Length(min = 20, max = 7000)
    @NotBlank
    private String description;

    @Column
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column
    private Boolean paid = false;

    @Column
    @PositiveOrZero
    private Integer participantLimit = 0;

    @Column
    private LocalDateTime publishedOn;

    @Column
    private Boolean requestModeration = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;

    @Column
    @Length(min = 3, max = 120)
    @NotBlank
    private String title;
}
