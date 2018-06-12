package rniesler.trainings.eventstore.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rniesler.trainings.eventstore.model.Aggregate;
import rniesler.trainings.eventstore.model.AggregateRepository;
import rniesler.trainings.eventstore.model.Event;
import rniesler.trainings.eventstore.model.EventRepository;

import java.time.LocalDateTime;

@Service
public class EventStoreService {
    private EventRepository eventRepository;
    private AggregateRepository aggregateRepository;

    public EventStoreService(EventRepository eventRepository, AggregateRepository aggregateRepository) {
        this.eventRepository = eventRepository;
        this.aggregateRepository = aggregateRepository;
    }

    @Transactional
    public void storeEvent(Event event) {
        //TODO validate event
        //TODO better exception
        Aggregate aggregate = aggregateRepository.findById(event.getId().getAggregateId()).orElseThrow(RuntimeException::new);
        if(aggregate.getVersion() != event.getId().getVersion()) {
            throw new RuntimeException(); //TODO in line with optimistic locking
        }
        eventRepository.save(event);
        aggregate.setLastModified(LocalDateTime.now());
        aggregateRepository.save(aggregate);
    }
}
