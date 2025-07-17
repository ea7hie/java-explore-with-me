package ru.yandex.practicum.event.dto.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithConfirmedRequests {
    private long id;
    private long requests;
}
