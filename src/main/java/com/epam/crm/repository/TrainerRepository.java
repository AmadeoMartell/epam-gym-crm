package com.epam.crm.repository;

import com.epam.crm.model.Trainer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);

    @EntityGraph(attributePaths = {"trainees"})
    Optional<Trainer> findWithTraineesByUsername(String username);
}
