package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
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
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final AuthenticationService authenticationService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final InputValidator inputValidator;

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        inputValidator.requireNonBlank(firstName, "firstName");
        inputValidator.requireNonBlank(lastName, "lastName");

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setActive(true);

        String username = usernameGenerator.generateUniqueUsername(
                firstName, lastName, traineeRepository::existsByUsername);
        String rawPassword = passwordGenerator.generatePassword();
        trainee.setUsername(username);
        trainee.setPassword(passwordEncoder.encode(rawPassword));

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee created = traineeRepository.save(trainee);
        created.setPassword(rawPassword);
        log.info("Created trainee profile: username={}", created.getUsername());
        return created;
    }

    public boolean matchCredentials(String username, String password) {
        boolean matched = authenticationService.traineeCredentialsMatch(username, password);
        log.debug("Trainee credential match for {}: {}", username, matched);
        return matched;
    }

    public Trainee getByUsername(String username, String password) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        log.info("Selected trainee profile: username={}", username);
        return trainee;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        inputValidator.requireNonBlank(newPassword, "newPassword");
        Trainee trainee = authenticationService.authenticateTrainee(username, oldPassword);
        trainee.setPassword(passwordEncoder.encode(newPassword));
        traineeRepository.update(trainee);
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

        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setActive(isActive);
        Trainee updated = traineeRepository.update(trainee);
        log.info("Updated trainee profile: username={}", username);
        return updated;
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainee trainee = authenticationService.authenticateTrainee(username, password);
        trainee.setActive(isActive);
        traineeRepository.update(trainee);
        log.info("Set trainee active={} for username={}", isActive, username);
    }

    public void deleteByUsername(String username, String password) {
        authenticationService.authenticateTrainee(username, password);
        traineeRepository.deleteByUsername(username);
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
        authenticationService.authenticateTrainee(username, password);
        List<Training> trainings = trainingRepository.findByTraineeCriteria(
                username, fromDate, toDate, trainerName, trainingTypeName);
        log.info("Retrieved {} trainings for trainee: username={}", trainings.size(), username);
        return trainings;
    }

    public List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        inputValidator.requireNonNull(trainerUsernames, "trainerUsernames");

        Trainee trainee = authenticationService.authenticateTrainee(username, password);

        List<Trainer> trainers = trainerUsernames.stream()
                .map(trainerUsername -> trainerRepository.findByUsername(trainerUsername)
                        .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername)))
                .collect(Collectors.toList());

        trainee.setTrainers(trainers);
        traineeRepository.update(trainee);
        log.info("Updated trainers list for trainee {}: {} trainer(s)", username, trainers.size());
        return trainee.getTrainers();
    }
}
