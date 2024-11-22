package com.eventscheduler.repository;

import com.eventscheduler.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.startTime <= :endTime AND e.endTime >= :startTime")
    List<Event> findEvents(@Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
}