package rniesler.trainings.eventstore.model;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AggregateRepository extends JpaRepository<Aggregate, UUID> {
}
