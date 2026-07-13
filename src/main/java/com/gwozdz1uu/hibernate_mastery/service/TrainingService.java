package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class TrainingService {

    private final TrainingDAO trainingDAO;
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    @PersistenceContext
    private EntityManager em;

    public TrainingService(TrainingDAO trainingDAO, TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    public Training addTraining(
            String traineeUsername,
            String trainerUsername,
            String trainingName,
            Long trainingTypeId,
            LocalDate trainingDate,
            int durationMinutes
    ) {
        Trainee trainee = traineeDAO.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + traineeUsername));
        Trainer trainer = trainerDAO.findBuUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername));
        TrainingType type = em.find(TrainingType.class, trainingTypeId);
        if (type == null) {
            throw new IllegalArgumentException("TrainingType not found: " + trainingTypeId);
        }

        Training training = new Training(
                null,
                trainee,
                trainer,
                trainingName,
                type,
                trainingDate,
                durationMinutes
        );
        trainee.getTrainings().add(training);
        return trainingDAO.create(training);
    }
}
