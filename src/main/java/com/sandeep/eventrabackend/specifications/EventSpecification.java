package com.sandeep.eventrabackend.specifications;

import com.sandeep.eventrabackend.model.Event;
import com.sandeep.eventrabackend.model.EventStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

// ── Issue #2096 — Listing all public events by searching and basis of status and category ──

public class EventSpecification {

    private EventSpecification() {}

    public static Specification<Event> publicEvents(
            String search,
            EventStatus status,
            String category) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isPublic")));

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";

                Predicate titleMatch       = cb.like(cb.lower(root.get("title")),       pattern);
                Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate locationMatch    = cb.like(cb.lower(root.get("location")),    pattern);

                predicates.add(cb.or(titleMatch, descriptionMatch, locationMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("category")),
                                category.trim().toLowerCase()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}