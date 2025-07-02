package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.StatisticDtoGet;
import ru.yandex.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;


public interface StatisticsRepository extends JpaRepository<Statistic, Long> {
    @Query("SELECT new ru.yandex.practicum.StatisticDtoGet(s.app, s.uri, " +
            "CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END) " +
            "FROM Statistic s " +
            "WHERE s.uri IN :uris " +
            "AND s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatisticDtoGet> findHitsByUriInAndTimestampBetween(
            @Param("uris") List<String> uris,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("unique") Boolean unique
    );

    @Query("SELECT new ru.yandex.practicum.StatisticDtoGet(s.app, s.uri, " +
            "CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END) " +
            "FROM Statistic s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatisticDtoGet> findHitsByTimestampBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("unique") Boolean unique
    );
}
