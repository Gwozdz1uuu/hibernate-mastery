package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.util.InputValidator;
import com.gwozdz1uu.hibernate_mastery.util.PasswordGenerator;
import com.gwozdz1uu.hibernate_mastery.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final TrainingDAO trainingDAO;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final InputValidator inputValidator;

    public TrainerService(
            TraineeDAO traineeDAO,
            TrainerDAO trainerDAO,
            TrainingDAO trainingDAO,
            UsernameGenerator usernameGenerator,
            PasswordGenerator passwordGenerator,
            InputValidator inputValidator
    ) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.trainingDAO = trainingDAO;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.inputValidator = inputValidator;
    }

    private Trainer authenticate(String username, String password) {
        Trainer trainer = trainerDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        if (!trainer.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid password for: " + username);
        }
        return trainer;
    }

    private Trainee authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        if (!trainee.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid password for: " + username);
        }
        return trainee;
    }

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        inputValidator.requireNonBlank(firstName, "firstName");
        inputValidator.requireNonBlank(lastName, "lastName");
        inputValidator.requireNonNull(specialization, "specialization");

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(true);
        trainer.setSpecialization(specialization);

        String username = usernameGenerator.generateUsername(firstName, lastName, trainerDAO.findAll());
        trainer.setUsername(username);
        trainer.setPassword(passwordGenerator.generatePassword());

        Trainer created = trainerDAO.create(trainer);
        log.info("Created trainer profile: username={}", created.getUsername());
        return created;
    }

    public boolean matchCredentials(String username, String password) {
        boolean matched = trainerDAO.findByUsername(username)
                .map(t -> Objects.equals(t.getPassword(), password))
                .orElse(false);
        log.debug("Trainer credential match for {}: {}", username, matched);
        return matched;
    }

    public Trainer getByUsername(String username, String password) {
        Trainer trainer = authenticate(username, password);
        log.info("Selected trainer profile: username={}", username);
        return trainer;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        inputValidator.requireNonBlank(newPassword, "newPassword");
        Trainer trainer = authenticate(username, oldPassword);
        trainer.setPassword(newPassword);
        trainerDAO.update(trainer);
        log.info("Changed password for trainer: username={}", username);
    }

    public Trainer updateProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            TrainingType specialization,
            boolean isActive
    ) {
        inputValidator.requireNonBlank(firstName, "firstName");
        inputValidator.requireNonBlank(lastName, "lastName");
        inputValidator.requireNonNull(specialization, "specialization");

        Trainer trainer = authenticate(username, password);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setActive(isActive);
        Trainer updated = trainerDAO.update(trainer);
        log.info("Updated trainer profile: username={}", username);
        return updated;
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainer trainer = authenticate(username, password);
        trainer.setActive(isActive);
        trainerDAO.update(trainer);
        log.info("Set trainer active={} for username={}", isActive, username);
    }

    public List<Training> getTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        authenticate(username, password);
        List<Training> trainings = trainingDAO.findByTrainerCriteria(username, fromDate, toDate, traineeName);
        log.info("Retrieved {} trainings for trainer: username={}", trainings.size(), username);
        return trainings;
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        authenticateTrainee(traineeUsername, traineePassword);
        List<Trainer> trainers = trainerDAO.findUnassignedToTrainee(traineeUsername);
        log.info("Retrieved {} unassigned trainers for trainee: username={}", trainers.size(), traineeUsername);
        return trainers;
    }
}
