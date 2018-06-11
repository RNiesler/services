package rniesler.trainings.domain;

import lombok.Data;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Data
public class EventLog {
    private UUID aggregateId;
    private Integer version;
    private String data;
    private String type;

    public EventLog() {
        //make JPA happy
    }
}
