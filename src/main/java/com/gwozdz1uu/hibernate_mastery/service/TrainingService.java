package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingTypeRepository;
import com.gwozdz1uu.hibernate_mastery.dto.AddTrainingRequest;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.util.DtoValidator;
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
    private final DtoValidator dtoValidator;

    public Training addTraining(AddTrainingRequest request) {
        dtoValidator.validate(request);

        Trainee trainee = authenticationService.authenticateTrainee(
                request.traineeUsername(), request.traineePassword());

        Trainer trainer = trainerRepository.findByUsername(request.trainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + request.trainerUsername()));
        TrainingType type = trainingTypeRepository.findById(request.trainingTypeId())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + request.trainingTypeId()));

        Training training = new Training(
                null,
                trainee,
                trainer,
                request.trainingName(),
                type,
                request.trainingDate(),
                request.durationMinutes()
        );
        trainee.getTrainings().add(training);
        Training created = trainingRepository.save(training);
        log.info("Added training '{}' for trainee {} with trainer {}",
                request.trainingName(), request.traineeUsername(), request.trainerUsername());
        return created;
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
        return addTraining(new AddTrainingRequest(
                traineeUsername, traineePassword, trainerUsername,
                trainingName, trainingTypeId, trainingDate, durationMinutes));
    }
}
