package com.gwozdz1uu.hibernate_mastery.facade;

import com.gwozdz1uu.hibernate_mastery.entity.*;
import com.gwozdz1uu.hibernate_mastery.service.TraineeService;
import com.gwozdz1uu.hibernate_mastery.service.TrainerService;
import com.gwozdz1uu.hibernate_mastery.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {

    private static final Logger log = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainer createTrainerProfile(String firstName, String lastName, TrainingType specialization) {
        log.debug("Creating trainer profile for {} {}", firstName, lastName);
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    public Trainee createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        log.debug("Creating trainee profile for {} {}", firstName, lastName);
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    public boolean authenticateTrainee(String username, String password) {
        return traineeService.matchCredentials(username, password);
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerService.matchCredentials(username, password);
    }

    public Trainer selectTrainerByUsername(String username, String password) {
        return trainerService.getByUsername(username, password);
    }

    public Trainee selectTraineeByUsername(String username, String password) {
        return traineeService.getByUsername(username, password);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public Trainer updateTrainerProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            TrainingType specialization,
            boolean isActive
    ) {
        return trainerService.updateProfile(username, password, firstName, lastName, specialization, isActive);
    }

    public Trainee updateTraineeProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address,
            boolean isActive
    ) {
        return traineeService.updateProfile(username, password, firstName, lastName, dateOfBirth, address, isActive);
    }

    public void activateOrDeactivateTrainee(String username, String password, boolean isActive) {
        traineeService.setActive(username, password, isActive);
    }

    public void activateOrDeactivateTrainer(String username, String password, boolean isActive) {
        trainerService.setActive(username, password, isActive);
    }

    public void deleteTraineeByUsername(String username, String password) {
        traineeService.deleteByUsername(username, password);
    }

    public List<Training> getTraineeTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    ) {
        return traineeService.getTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Training> getTrainerTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        return trainerService.getTrainings(username, password, fromDate, toDate, traineeName);
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
        return trainingService.addTraining(
                traineeUsername,
                traineePassword,
                trainerUsername,
                trainingName,
                trainingTypeId,
                trainingDate,
                durationMinutes
        );
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        return trainerService.getUnassignedTrainers(traineeUsername, traineePassword);
    }

    public List<Trainer> updateTraineeTrainersList(
            String traineeUsername,
            String traineePassword,
            List<String> trainerUsernames
    ) {
        return traineeService.updateTrainersList(traineeUsername, traineePassword, trainerUsernames);
    }
}
