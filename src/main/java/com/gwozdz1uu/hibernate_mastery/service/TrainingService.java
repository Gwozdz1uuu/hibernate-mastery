package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.util.InputValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDAO trainingDAO;
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final InputValidator inputValidator;

    @PersistenceContext
    private EntityManager em;

    public TrainingService(
            TrainingDAO trainingDAO,
            TraineeDAO traineeDAO,
            TrainerDAO trainerDAO,
            InputValidator inputValidator
    ) {
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.inputValidator = inputValidator;
    }

    public Training addTraining(
            String traineeUsername,
            String traineePassword,
            String trainerUsername,
            String trainingName,
            Long trainingTypeId,
            LocalDate trainingDate,
            int durationMinutes
    ) {
        inputValidator.requireNonBlank(traineeUsername, "traineeUsername");
        inputValidator.requireNonBlank(traineePassword, "traineePassword");
        inputValidator.requireNonBlank(trainerUsername, "trainerUsername");
        inputValidator.requireNonBlank(trainingName, "trainingName");
        inputValidator.requireNonNull(trainingTypeId, "trainingTypeId");
        inputValidator.requireNonNull(trainingDate, "trainingDate");
        inputValidator.requirePositive(durationMinutes, "durationMinutes");

        Trainee trainee = traineeDAO.findByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeUsername));
        if (!Objects.equals(trainee.getPassword(), traineePassword)) {
            throw new AuthenticationException("Invalid password for: " + traineeUsername);
        }

        Trainer trainer = trainerDAO.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));
        TrainingType type = em.find(TrainingType.class, trainingTypeId);
        if (type == null) {
            throw new EntityNotFoundException("TrainingType not found: " + trainingTypeId);
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
        Training created = trainingDAO.create(training);
        log.info("Added training '{}' for trainee {} with trainer {}", trainingName, traineeUsername, trainerUsername);
        return created;
    }
}
