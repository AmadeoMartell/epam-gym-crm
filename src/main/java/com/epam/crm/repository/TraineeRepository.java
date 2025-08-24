package com.epam.crm.repository;

import com.epam.crm.model.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);

    @EntityGraph(attributePaths = {"trainers"})
    Optional<Trainee> findWithTrainersByUsername(String username);
}
