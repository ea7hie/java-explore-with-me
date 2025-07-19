package ru.yandex.practicum.event.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.State;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(Long id);

    List<Event> findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String annotation, String description);

    List<Event> findAllByState(State state);

    List<Event> findAllByInitiatorId(long initiatorId);

    Optional<Event> findByInitiatorIdAndId(long initiatorId, long id);

    List<Event> findAllByInitiatorIdIn(List<Long> initiatorIds);
}
