package com.epam.crm.repository;

import com.epam.crm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<Training> findForTraineeWithCriteria(
            @Param("traineeUsername") String traineeUsername,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerName") String trainerName,
            @Param("typeName") String typeName
    );


    @Query("""
       select t from Training t
         join t.trainer tr
         join t.trainee trn
       where tr.username = :trainerUsername
         and (:from is null or t.date >= :from)
         and (:to is null or t.date <= :to)
         and (:traineeName is null or lower(concat(trn.firstName,' ',trn.lastName)) like lower(concat('%', :traineeName, '%')))
       """)
    List<Training> findForTrainerWithCriteria(
            @Param("trainerUsername") String trainerUsername,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("traineeName") String traineeName
    );

}

