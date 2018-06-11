package rniesler.trainings;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import rniesler.trainings.eventstore.domain.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbTest {
    @Autowired
    AggregateRepository aggregateRepository;

    @Autowired
    EventRepository eventRepository;

    @Test
    public void testAggregateSave() {
        Aggregate newAggregate = new Aggregate();
        newAggregate.setLastModified(LocalDateTime.now());
        newAggregate.setType("A");
        aggregateRepository.save(newAggregate);
        Assert.assertTrue(newAggregate.getId() != null);
        Assert.assertTrue(newAggregate.getVersion() == 0);
    }

    @Test
    public void testEventSave() {
        Aggregate newAggregate = new Aggregate();
        newAggregate.setLastModified(LocalDateTime.now());
        newAggregate.setType("A");
        aggregateRepository.save(newAggregate);
        Event newEvent = new Event();
        EventIdentification id = new EventIdentification();
        id.setAggregateId(newAggregate.getId());
        id.setVersion(newAggregate.getVersion() + 1);
        newEvent.setId(id);
        newEvent.setType("B");
        eventRepository.save(newEvent);
    }

    @Test
    public void testEventWithWrongAggregateId() {
        Event newEvent = new Event();
        EventIdentification id = new EventIdentification();
        id.setAggregateId(UUID.randomUUID());
        id.setVersion(1);
        newEvent.setId(id);
        newEvent.setType("B");
        try {
            eventRepository.saveAndFlush(newEvent);
            Assert.fail("DataIntegrityViolationException was expected");
        } catch (DataIntegrityViolationException exception) {
            // expected
        }
    }
}
