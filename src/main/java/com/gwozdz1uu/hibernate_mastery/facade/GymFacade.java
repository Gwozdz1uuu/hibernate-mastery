package com.gwozdz1uu.hibernate_mastery.facade;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.service.TraineeService;
import com.gwozdz1uu.hibernate_mastery.service.TrainerService;
import com.gwozdz1uu.hibernate_mastery.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GymFacade {

    private static final Logger logger = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // Trainee operations

    public Trainee createTrainee(Trainee trainee) {
        logger.info("Facade: creating trainee profile");
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        logger.info("Facade: updating trainee profile with id: {}", trainee.getId());
        return traineeService.updateTrainee(trainee);
    }

    public boolean deleteTrainee(Long id) {
        logger.info("Facade: deleting trainee profile with id: {}", id);
        return traineeService.deleteTrainee(id);
    }

    public Optional<Trainee> selectTrainee(Long id) {
        logger.info("Facade: selecting trainee profile with id: {}", id);
        return traineeService.selectTrainee(id);
    }

    public List<Trainee> selectAllTrainees() {
        logger.info("Facade: selecting all trainees");
        return traineeService.selectAllTrainees();
    }

    // Trainer operations

    public Trainer createTrainer(Trainer trainer) {
        logger.info("Facade: creating trainer profile");
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        logger.info("Facade: updating trainer profile with id: {}", trainer.getId());
        return trainerService.updateTrainer(trainer);
    }

    public Optional<Trainer> selectTrainer(Long id) {
        logger.info("Facade: selecting trainer profile with id: {}", id);
        return trainerService.selectTrainer(id);
    }

    public List<Trainer> selectAllTrainers() {
        logger.info("Facade: selecting all trainers");
        return trainerService.selectAllTrainers();
    }

    // Training operations

    public Training createTraining(Training training) {
        logger.info("Facade: creating training");
        return trainingService.createTraining(training);
    }

    public Optional<Training> selectTraining(Long id) {
        logger.info("Facade: selecting training with id: {}", id);
        return trainingService.selectTraining(id);
    }

    public List<Training> selectAllTrainings() {
        logger.info("Facade: selecting all trainings");
        return trainingService.selectAllTrainings();
    }
}
