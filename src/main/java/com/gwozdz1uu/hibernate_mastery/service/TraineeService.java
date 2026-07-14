package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final TrainingDAO trainingDAO;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final InputValidator inputValidator;

    public TraineeService(
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

    private Trainee authenticate(String username, String password) {
        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
        if (!trainee.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password for: " + username);
        }
        return trainee;
    }

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        inputValidator.requireNonBlank(firstName, "firstName");
        inputValidator.requireNonBlank(lastName, "lastName");

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setActive(true);

        String username = usernameGenerator.generateUsername(firstName, lastName, traineeDAO.findAll());
        trainee.setUsername(username);
        trainee.setPassword(passwordGenerator.generatePassword());

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee created = traineeDAO.create(trainee);
        log.info("Created trainee profile: username={}", created.getUsername());
        return created;
    }

    public boolean matchCredentials(String username, String password) {
        boolean matched = traineeDAO.findByUsername(username)
                .map(t -> Objects.equals(t.getPassword(), password))
                .orElse(false);
        log.debug("Trainee credential match for {}: {}", username, matched);
        return matched;
    }

    public Trainee getByUsername(String username, String password) {
        Trainee trainee = authenticate(username, password);
        log.info("Selected trainee profile: username={}", username);
        return trainee;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        inputValidator.requireNonBlank(newPassword, "newPassword");
        Trainee trainee = authenticate(username, oldPassword);
        trainee.setPassword(newPassword);
        traineeDAO.update(trainee);
        log.info("Changed password for trainee: username={}", username);
    }

    public Trainee updateProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address,
            boolean isActive
    ) {
        inputValidator.requireNonBlank(firstName, "firstName");
        inputValidator.requireNonBlank(lastName, "lastName");

        Trainee trainee = authenticate(username, password);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setActive(isActive);
        Trainee updated = traineeDAO.update(trainee);
        log.info("Updated trainee profile: username={}", username);
        return updated;
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainee trainee = authenticate(username, password);
        trainee.setActive(isActive);
        traineeDAO.update(trainee);
        log.info("Set trainee active={} for username={}", isActive, username);
    }

    public void deleteByUsername(String username, String password) {
        authenticate(username, password);
        traineeDAO.deleteByUsername(username);
        log.info("Deleted trainee profile: username={}", username);
    }

    public List<Training> getTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    ) {
        authenticate(username, password);
        List<Training> trainings = trainingDAO.findByTraineeCriteria(
                username, fromDate, toDate, trainerName, trainingTypeName);
        log.info("Retrieved {} trainings for trainee: username={}", trainings.size(), username);
        return trainings;
    }

    public List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        inputValidator.requireNonNull(trainerUsernames, "trainerUsernames");

        Trainee trainee = authenticate(username, password);

        List<Trainer> trainers = trainerUsernames.stream()
                .map(trainerUsername -> trainerDAO.findByUsername(trainerUsername)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername)))
                .collect(Collectors.toList());

        trainee.setTrainers(trainers);
        traineeDAO.update(trainee);
        log.info("Updated trainers list for trainee {}: {} trainer(s)", username, trainers.size());
        return trainee.getTrainers();
    }
}
