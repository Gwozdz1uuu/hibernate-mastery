package com.gwozdz1uu.hibernate_mastery.facade;

import com.gwozdz1uu.hibernate_mastery.dto.*;
import com.gwozdz1uu.hibernate_mastery.entity.*;
import com.gwozdz1uu.hibernate_mastery.service.TraineeService;
import com.gwozdz1uu.hibernate_mastery.service.TrainerService;
import com.gwozdz1uu.hibernate_mastery.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

@Component
public class GymFacade {

    private static final Logger log = LoggerFactory.getLogger(GymFacade.class);
    private static final String MDC_USERNAME = "username";

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainer createTrainerProfile(String firstName, String lastName, TrainingType specialization) {
        log.debug("action=createTrainerProfile, firstName={}, lastName={}", firstName, lastName);
        return trainerService.createTrainer(new CreateTrainerRequest(firstName, lastName, specialization));
    }

    public Trainee createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        log.debug("action=createTraineeProfile, firstName={}, lastName={}", firstName, lastName);
        return traineeService.createTrainee(new CreateTraineeRequest(firstName, lastName, dateOfBirth, address));
    }

    public boolean authenticateTrainee(String username, String password) {
        return withUsernameContext(username, () -> traineeService.matchCredentials(username, password));
    }

    public boolean authenticateTrainer(String username, String password) {
        return withUsernameContext(username, () -> trainerService.matchCredentials(username, password));
    }

    public Trainer selectTrainerByUsername(String username, String password) {
        return withUsernameContext(username, () -> trainerService.getByUsername(username, password));
    }

    public Trainee selectTraineeByUsername(String username, String password) {
        return withUsernameContext(username, () -> traineeService.getByUsername(username, password));
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        withUsernameContext(username, () -> {
            traineeService.changePassword(new ChangePasswordRequest(username, oldPassword, newPassword));
            return null;
        });
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        withUsernameContext(username, () -> {
            trainerService.changePassword(new ChangePasswordRequest(username, oldPassword, newPassword));
            return null;
        });
    }

    public Trainer updateTrainerProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            TrainingType specialization,
            boolean isActive
    ) {
        return withUsernameContext(username, () -> trainerService.updateProfile(
                new UpdateTrainerRequest(username, password, firstName, lastName, specialization, isActive)));
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
        return withUsernameContext(username, () -> traineeService.updateProfile(
                new UpdateTraineeRequest(username, password, firstName, lastName, dateOfBirth, address, isActive)));
    }

    public void activateOrDeactivateTrainee(String username, String password, boolean isActive) {
        withUsernameContext(username, () -> {
            traineeService.setActive(username, password, isActive);
            return null;
        });
    }

    public void activateOrDeactivateTrainer(String username, String password, boolean isActive) {
        withUsernameContext(username, () -> {
            trainerService.setActive(username, password, isActive);
            return null;
        });
    }

    public void deleteTraineeByUsername(String username, String password) {
        withUsernameContext(username, () -> {
            traineeService.deleteByUsername(username, password);
            return null;
        });
    }

    public List<Training> getTraineeTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    ) {
        return withUsernameContext(username, () -> traineeService.getTrainings(
                new TraineeTrainingSearchRequest(username, password, fromDate, toDate, trainerName, trainingTypeName)));
    }

    public List<Training> getTrainerTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        return withUsernameContext(username, () -> trainerService.getTrainings(
                new TrainerTrainingSearchRequest(username, password, fromDate, toDate, traineeName)));
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
        return withUsernameContext(traineeUsername, () -> trainingService.addTraining(new AddTrainingRequest(
                traineeUsername, traineePassword, trainerUsername,
                trainingName, trainingTypeId, trainingDate, durationMinutes)));
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        return withUsernameContext(traineeUsername,
                () -> trainerService.getUnassignedTrainers(traineeUsername, traineePassword));
    }

    public List<Trainer> updateTraineeTrainersList(
            String traineeUsername,
            String traineePassword,
            List<String> trainerUsernames
    ) {
        return withUsernameContext(traineeUsername, () -> traineeService.updateTrainersList(
                new UpdateTrainersListRequest(traineeUsername, traineePassword, trainerUsernames)));
    }

    private <T> T withUsernameContext(String username, Supplier<T> action) {
        MDC.put(MDC_USERNAME, username);
        try {
            return action.get();
        } finally {
            MDC.remove(MDC_USERNAME);
        }
    }
}
