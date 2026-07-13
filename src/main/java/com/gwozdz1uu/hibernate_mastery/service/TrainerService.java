package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.util.PasswordGenerator;
import com.gwozdz1uu.hibernate_mastery.util.UsernameGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TrainerService {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final TrainingDAO trainingDAO;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TrainerService(
            TraineeDAO traineeDAO,
            TrainerDAO trainerDAO,
            TrainingDAO trainingDAO,
            UsernameGenerator usernameGenerator,
            PasswordGenerator passwordGenerator
    ) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.trainingDAO = trainingDAO;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    private Trainer authenticate(String username, String password) {
        Trainer trainer = trainerDAO.findBuUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));
        if (!trainer.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password for: " + username);
        }
        return trainer;
    }

    private Trainee authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
        if (!trainee.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password for: " + username);
        }
        return trainee;
    }

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(true);
        trainer.setSpecialization(specialization);

        String username = usernameGenerator.generateUsername(firstName, lastName, trainerDAO.findAll());
        trainer.setUsername(username);
        trainer.setPassword(passwordGenerator.generatePassword());

        return trainerDAO.create(trainer);
    }

    public boolean matchCredentials(String username, String password) {
        return trainerDAO.findBuUsername(username)
                .map(t -> Objects.equals(t.getPassword(), password))
                .orElse(false);
    }

    public Trainer getByUsername(String username, String password) {
        return authenticate(username, password);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = authenticate(username, oldPassword);
        trainer.setPassword(newPassword);
        trainerDAO.update(trainer);
    }

    public Trainer updateProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            TrainingType specialization,
            boolean isActive
    ) {
        Trainer trainer = authenticate(username, password);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setActive(isActive);
        return trainerDAO.update(trainer);
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainer trainer = authenticate(username, password);
        trainer.setActive(isActive);
        trainerDAO.update(trainer);
    }

    public List<Training> getTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        authenticate(username, password);
        return trainingDAO.findByTrainerCriteria(username, fromDate, toDate, traineeName);
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        authenticateTrainee(traineeUsername, traineePassword);
        return trainerDAO.findUnassignedToTrainee(traineeUsername);
    }
}
