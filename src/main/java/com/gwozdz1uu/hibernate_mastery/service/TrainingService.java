package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingTypeRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.util.InputValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final AuthenticationService authenticationService;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final InputValidator inputValidator;

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

        Trainee trainee = authenticationService.authenticateTrainee(traineeUsername, traineePassword);

        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));
        TrainingType type = trainingTypeRepository.findById(trainingTypeId)
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + trainingTypeId));

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
        Training created = trainingRepository.save(training);
        log.info("Added training '{}' for trainee {} with trainer {}", trainingName, traineeUsername, trainerUsername);
        return created;
    }
}
