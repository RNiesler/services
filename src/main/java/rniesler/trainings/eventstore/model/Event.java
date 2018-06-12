package rniesler.trainings.eventstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @EmbeddedId
    private EventIdentification id;
    private String data;
    private String type;

    public Event(UUID aggregateId, Integer version, String data, String type) {
        this.id = new EventIdentification(aggregateId, version);
        this.data = data;
        this.type = type;
    }
}
