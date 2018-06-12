package rniesler.trainings.integration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rniesler.trainings.TrainingsApplication;
import rniesler.trainings.eventstore.model.Aggregate;
import rniesler.trainings.eventstore.model.AggregateRepository;
import rniesler.trainings.eventstore.model.Event;
import rniesler.trainings.eventstore.services.EventStoreService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

@ActiveProfiles("spied-test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class EventStoreServiceIntegrationTest {
    @Profile("spied-test")
    @Configuration
    @Import(TrainingsApplication.class)
    public static class AggregateRepositorySpyConfiguration {
        class SpiedAggregateRepository implements AggregateRepository {
            SpiedAggregateRepository(AggregateRepository aggregateRepository, CountDownLatch countDownLatch) {
                this.aggregateRepository = aggregateRepository;
                this.countDownLatch = countDownLatch;
            }

            private AggregateRepository aggregateRepository;
            private CountDownLatch countDownLatch;

            public Aggregate save(Aggregate entity) {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    Assert.fail("Unexpected InterruptedException");
                }
                return aggregateRepository.save(entity);
            }

            @Override
            public <S extends Aggregate> S saveAndFlush(S entity) {
                return aggregateRepository.saveAndFlush(entity);
            }

            @Override
            public Optional<Aggregate> findById(UUID uuid) {
                return aggregateRepository.findById(uuid);
            }

            @Override
            public List<Aggregate> findAll() {
                return null;
            }

            @Override
            public List<Aggregate> findAll(Sort sort) {
                return null;
            }

            @Override
            public Page<Aggregate> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public List<Aggregate> findAllById(Iterable<UUID> uuids) {
                return null;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(UUID uuid) {

            }

            @Override
            public void delete(Aggregate entity) {

            }

            @Override
            public void deleteAll(Iterable<? extends Aggregate> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public <S extends Aggregate> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public boolean existsById(UUID uuid) {
                return false;
            }

            @Override
            public void flush() {

            }

            @Override
            public void deleteInBatch(Iterable<Aggregate> entities) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public Aggregate getOne(UUID uuid) {
                return null;
            }

            @Override
            public <S extends Aggregate> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends Aggregate> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends Aggregate> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public <S extends Aggregate> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends Aggregate> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends Aggregate> boolean exists(Example<S> example) {
                return false;
            }
        }

        @Bean
        @Primary
        public AggregateRepository aggregateRepositorySpy(AggregateRepository aggregateRepository) {
            return new SpiedAggregateRepository(aggregateRepository, countDownLatch());
        }

        @Bean
        CountDownLatch countDownLatch() {
            return new CountDownLatch(2);
        }
    }

    @Autowired
    EventStoreService eventStoreService;

    @Autowired
    AggregateRepository aggregateRepository;

    @Autowired
    CountDownLatch countDownLatch;

    @Test
    public void testOptimisticLocking() throws InterruptedException {
        Aggregate aggregate = new Aggregate();
        aggregate.setType("A");
        aggregateRepository.saveAndFlush(aggregate);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Event> task = () -> {
            Event event = new Event(aggregate.getId(), aggregate.getVersion(), "", "");
            eventStoreService.storeEvent(event);
            return event;
        };
        List<Future<Event>> tasks = executorService.invokeAll(List.of(task, task));
        int optimisticLockingFailureCount = tasks.stream().mapToInt(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                Assert.fail("Optimistic locking exception expected but got " + e.getClass());
            } catch (ExecutionException e) {
                return e.getCause() instanceof  ObjectOptimisticLockingFailureException ? 1 : 0;
            }
            return 0;
        }).sum();
        Assert.assertEquals("Expect single Optimistic Locking failure", 1, optimisticLockingFailureCount);

    }
}
