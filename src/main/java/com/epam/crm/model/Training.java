package com.epam.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainee_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_training_trainee"))
    private Trainee trainee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_training_trainer"))
    private Trainer trainer;

    @NotBlank
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_training_type"))
    private TrainingType trainingType;

    @NotNull
    @Column(name = "training_date", nullable = false)
    private LocalDate date;

    @NotNull
    @Min(1)
    @Column(name = "duration", nullable = false)
    private Integer duration;
}
