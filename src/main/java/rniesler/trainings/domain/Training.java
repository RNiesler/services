package rniesler.trainings.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Training {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
