package com.gwozdz1uu.hibernate_mastery.facade;

import com.gwozdz1uu.hibernate_mastery.entity.*;
import com.gwozdz1uu.hibernate_mastery.service.TraineeService;
import com.gwozdz1uu.hibernate_mastery.service.TrainerService;
import com.gwozdz1uu.hibernate_mastery.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // (1) Create trainer profile
    public Trainer createTrainerProfile(String firstName, String lastName, TrainingType specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    // (2) Create trainee profile
    public Trainee createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    // (3) Authenticate trainee
    public boolean authenticateTrainee(String username, String password) {
        return traineeService.matchCredentials(username, password);
    }

    // (4) Authenticate trainer
    public boolean authenticateTrainer(String username, String password) {
        return trainerService.matchCredentials(username, password);
    }

    // (5) Select trainer by username
    public Trainer selectTrainerByUsername(String username, String password) {
        return trainerService.getByUsername(username, password);
    }

    // (6) Select trainee by username
    public Trainee selectTraineeByUsername(String username, String password) {
        return traineeService.getByUsername(username, password);
    }

    // (7) Change trainee password
    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    // (8) Change trainer password
    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    // (9) Update trainer profile
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

    // (10) Update trainee profile
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

    // (11) Activate/deactivate trainee
    public void activateOrDeactivateTrainee(String username, String password, boolean isActive) {
        traineeService.setActive(username, password, isActive);
    }

    // (12) Activate/deactivate trainer
    public void activateOrDeactivateTrainer(String username, String password, boolean isActive) {
        trainerService.setActive(username, password, isActive);
    }

    // (13) Delete trainee by username
    public void deleteTraineeByUsername(String username, String password) {
        traineeService.deleteByUsername(username, password);
    }

    // (14) Get trainee trainings by criteria
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

    // (15) Get trainer trainings by criteria
    public List<Training> getTrainerTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        return trainerService.getTrainings(username, password, fromDate, toDate, traineeName);
    }

    // (16) Add training
    public Training addTraining(
            String traineeUsername,
            String trainerUsername,
            String trainingName,
            Long trainingTypeId,
            LocalDate trainingDate,
            int durationMinutes
    ) {
        return trainingService.addTraining(
                traineeUsername,
                trainerUsername,
                trainingName,
                trainingTypeId,
                trainingDate,
                durationMinutes
        );
    }

    // (17) Trainers not assigned to trainee
    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        return trainerService.getUnassignedTrainers(traineeUsername, traineePassword);
    }

    // (18) Update trainee trainers list
    public List<Trainer> updateTraineeTrainersList(
            String traineeUsername,
            String traineePassword,
            List<String> trainerUsernames
    ) {
        return traineeService.updateTrainersList(traineeUsername, traineePassword, trainerUsernames);
    }
}
