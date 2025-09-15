package com.epam.crm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Trainer extends User {

    @Column(name = "specialization", length = 100)
    private String specialization;

    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<Training> trainings = new HashSet<>();

    public void addTrainee(Trainee t) {
        trainees.add(t);
        t.getTrainers().add(this);
    }

    public void removeTrainee(Trainee t) {
        trainees.remove(t);
        t.getTrainers().remove(this);
    }
}
