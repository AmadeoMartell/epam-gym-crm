package com.epam.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Trainee extends User {

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 255)
    private String address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_trainee_trainer_pair", columnNames = {"trainee_id", "trainer_id"})
    )
    @Builder.Default
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(
            mappedBy = "trainee",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @Builder.Default
    private Set<Training> trainings = new HashSet<>();

    public void addTrainer(Trainer tr) {
        trainers.add(tr);
        tr.getTrainees().add(this);
    }

    public void removeTrainer(Trainer tr) {
        trainers.remove(tr);
        tr.getTrainees().remove(this);
    }
}
