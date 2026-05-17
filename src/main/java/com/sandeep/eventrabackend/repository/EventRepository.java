package com.sandeep.eventrabackend.repository;

import com.sandeep.eventrabackend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndIsPublicTrue(Long id);
}
