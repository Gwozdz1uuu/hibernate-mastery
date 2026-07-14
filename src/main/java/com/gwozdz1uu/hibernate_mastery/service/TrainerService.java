package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.security.PasswordEncoder;
import com.gwozdz1uu.hibernate_mastery.util.InputValidator;
import com.gwozdz1uu.hibernate_mastery.util.PasswordGenerator;
import com.gwozdz1uu.hibernate_mastery.util.UsernameGenerator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final AuthenticationService authenticationService;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final InputValidator inputValidator;

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        inputValidator.requireNonBlank(firstName, "firstName");
        inputValidator.requireNonBlank(lastName, "lastName");
        inputValidator.requireNonNull(specialization, "specialization");

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(true);
        trainer.setSpecialization(specialization);

        String username = usernameGenerator.generateUniqueUsername(
                firstName, lastName, trainerRepository::existsByUsername);
        String rawPassword = passwordGenerator.generatePassword();
        trainer.setUsername(username);
        trainer.setPassword(passwordEncoder.encode(rawPassword));

        Trainer created = trainerRepository.save(trainer);
        created.setPassword(rawPassword);
        log.info("Created trainer profile: username={}", created.getUsername());
        return created;
    }

    public boolean matchCredentials(String username, String password) {
        boolean matched = authenticationService.trainerCredentialsMatch(username, password);
        log.debug("Trainer credential match for {}: {}", username, matched);
        return matched;
    }

    public Trainer getByUsername(String username, String password) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        log.info("Selected trainer profile: username={}", username);
        return trainer;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        inputValidator.requireNonBlank(newPassword, "newPassword");
        Trainer trainer = authenticationService.authenticateTrainer(username, oldPassword);
        trainer.setPassword(passwordEncoder.encode(newPassword));
        trainerRepository.update(trainer);
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

        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setActive(isActive);
        Trainer updated = trainerRepository.update(trainer);
        log.info("Updated trainer profile: username={}", username);
        return updated;
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.setActive(isActive);
        trainerRepository.update(trainer);
        log.info("Set trainer active={} for username={}", isActive, username);
    }

    public List<Training> getTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        authenticationService.authenticateTrainer(username, password);
        List<Training> trainings = trainingRepository.findByTrainerCriteria(username, fromDate, toDate, traineeName);
        log.info("Retrieved {} trainings for trainer: username={}", trainings.size(), username);
        return trainings;
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        authenticationService.authenticateTrainee(traineeUsername, traineePassword);
        List<Trainer> trainers = trainerRepository.findUnassignedToTrainee(traineeUsername);
        log.info("Retrieved {} unassigned trainers for trainee: username={}", trainers.size(), traineeUsername);
        return trainers;
    }
}
