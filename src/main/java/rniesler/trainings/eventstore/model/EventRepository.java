package rniesler.trainings.eventstore.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, EventIdentification> {
}
