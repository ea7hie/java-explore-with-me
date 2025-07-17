package ru.yandex.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.event.dto.get.EventWithConfirmedRequests;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterIdIn(List<Long> requesterIds);

    @Query("SELECT COUNT(DISTINCT r.requester.id)" +
            "FROM Request r " +
            "WHERE r.event.id = :eventId " +
            "AND r.status = :status")
    long getConfirmedRequests(@Param("eventId") long eventId, @Param("status") Status status);

    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    List<Request> findAllByRequesterId(long requesterId);

    List<Request> findAllByEventId(long eventId);

    @Query("SELECT new ru.yandex.practicum.event.dto.get.EventWithConfirmedRequests(" +
            "r.event.id, COUNT(r.requester.id)) " +
            "FROM Request r WHERE r.event.id IN (:eventIds) " +
            "AND r.status = :status " +
            "GROUP BY r.event.id")
    List<EventWithConfirmedRequests> getConfirmedRequests(List<Long> eventIds, Status status);
}
