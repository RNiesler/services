package rniesler.trainings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rniesler.trainings.eventstore.model.Aggregate;
import rniesler.trainings.eventstore.model.AggregateRepository;
import rniesler.trainings.eventstore.model.Event;
import rniesler.trainings.eventstore.model.EventRepository;
import rniesler.trainings.eventstore.services.EventStoreService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventStoreUnitTest {

    private EventStoreService eventStoreService;
    private AggregateRepository aggregateRepository;
    private EventRepository eventRepository;

    @BeforeEach
    public void setup() {
        eventRepository = mock(EventRepository.class);
        aggregateRepository = mock(AggregateRepository.class);
        eventStoreService = new EventStoreService(eventRepository, aggregateRepository);
    }

    @Test
    public void testVersionChecking() {
        UUID uuid = UUID.randomUUID();
        Aggregate aggregate = new Aggregate(uuid, "", LocalDateTime.now(), 1);
        when(aggregateRepository.findById(uuid)).thenReturn(Optional.of(aggregate));
        when(aggregateRepository.save(aggregate)).thenAnswer(invocation -> {
            aggregate.setVersion(aggregate.getVersion() + 1);
            return aggregate;
        });
        Event event1 = new Event(uuid, aggregate.getVersion(), "", "A");
        Event event2 = new Event(uuid, aggregate.getVersion(), "", "B");
        eventStoreService.storeEvent(event1);
        assertThrows(RuntimeException.class, () -> eventStoreService.storeEvent(event2));
    }
}
