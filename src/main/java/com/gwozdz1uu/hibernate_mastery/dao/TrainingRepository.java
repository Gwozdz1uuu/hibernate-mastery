package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository {
    Training save(Training training);
    List<Training> findByTraineeCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    );
    List<Training> findByTrainerCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );
}
