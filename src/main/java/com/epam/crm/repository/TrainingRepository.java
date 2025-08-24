package com.epam.crm.repository;

import com.epam.crm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
       select t from Training t
         join t.trainee trn
         join t.trainer tr
         join t.trainingType tt
       where trn.username = :traineeUsername
         and (:from is null or t.date >= :from)
         and (:to is null or t.date <= :to)
         and (:trainerName is null or lower(concat(tr.firstName,' ',tr.lastName)) like lower(concat('%', :trainerName, '%')))
         and (:typeName is null or tt.name = :typeName)
       """)
    List<Training> findForTraineeWithCriteria(String traineeUsername,
                                              LocalDate from,
                                              LocalDate to,
                                              String trainerName,
                                              String typeName);


    @Query("""
       select t from Training t
         join t.trainer tr
         join t.trainee trn
       where tr.username = :trainerUsername
         and (:from is null or t.date >= :from)
         and (:to is null or t.date <= :to)
         and (:traineeName is null or lower(concat(trn.firstName,' ',trn.lastName)) like lower(concat('%', :traineeName, '%')))
       """)
    List<Training> findForTrainerWithCriteria(String trainerUsername,
                                              LocalDate from,
                                              LocalDate to,
                                              String traineeName);

}

