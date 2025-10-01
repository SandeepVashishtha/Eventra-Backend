
package com.eventra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventra.entity.Event;

public interface EventsRepository extends JpaRepository<Event,Long>{
    
}
