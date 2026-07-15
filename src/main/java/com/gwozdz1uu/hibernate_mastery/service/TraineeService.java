package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.dto.*;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.security.PasswordEncoder;
import com.gwozdz1uu.hibernate_mastery.util.DtoValidator;
import com.gwozdz1uu.hibernate_mastery.util.ProfileCreationHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final AuthenticationService authenticationService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileCreationHelper profileCreationHelper;
    private final PasswordEncoder passwordEncoder;
    private final DtoValidator dtoValidator;

    public Trainee createTrainee(CreateTraineeRequest request) {
        dtoValidator.validate(request);

        Trainee trainee = new Trainee();
        String rawPassword = profileCreationHelper.setupNewProfile(
                trainee, request.firstName(), request.lastName(), traineeRepository::existsByUsername);
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());

        Trainee created = traineeRepository.save(trainee);
        created.setPassword(rawPassword);
        log.info("Created trainee profile: username={}", created.getUsername());
        return created;
    }

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return createTrainee(new CreateTraineeRequest(firstName, lastName, dateOfBirth, address));
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

    public void changePassword(ChangePasswordRequest request) {
        dtoValidator.validate(request);
        Trainee trainee = authenticationService.authenticateTrainee(request.username(), request.oldPassword());
        trainee.setPassword(passwordEncoder.encode(request.newPassword()));
        traineeRepository.update(trainee);
        log.info("Changed password for trainee: username={}", request.username());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        changePassword(new ChangePasswordRequest(username, oldPassword, newPassword));
    }

    public Trainee updateProfile(UpdateTraineeRequest request) {
        dtoValidator.validate(request);

        Trainee trainee = authenticationService.authenticateTrainee(request.username(), request.password());
        trainee.setFirstName(request.firstName());
        trainee.setLastName(request.lastName());
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
        trainee.setActive(request.active());
        Trainee updated = traineeRepository.update(trainee);
        log.info("Updated trainee profile: username={}", request.username());
        return updated;
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
        return updateProfile(new UpdateTraineeRequest(
                username, password, firstName, lastName, dateOfBirth, address, isActive));
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

    public List<Training> getTrainings(TraineeTrainingSearchRequest request) {
        authenticationService.authenticateTrainee(request.username(), request.password());
        List<Training> trainings = trainingRepository.findByTraineeCriteria(
                request.username(), request.fromDate(), request.toDate(),
                request.trainerName(), request.trainingTypeName());
        log.info("Retrieved {} trainings for trainee: username={}", trainings.size(), request.username());
        return trainings;
    }

    public List<Training> getTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName
    ) {
        return getTrainings(new TraineeTrainingSearchRequest(
                username, password, fromDate, toDate, trainerName, trainingTypeName));
    }

    public List<Trainer> updateTrainersList(UpdateTrainersListRequest request) {
        dtoValidator.validate(request);

        Trainee trainee = authenticationService.authenticateTrainee(
                request.traineeUsername(), request.traineePassword());

        List<Trainer> trainers = request.trainerUsernames().stream()
                .map(trainerUsername -> trainerRepository.findByUsername(trainerUsername)
                        .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername)))
                .collect(Collectors.toList());

        trainee.setTrainers(trainers);
        traineeRepository.update(trainee);
        log.info("Updated trainers list for trainee {}: {} trainer(s)",
                request.traineeUsername(), trainers.size());
        return trainee.getTrainers();
    }

    public List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        return updateTrainersList(new UpdateTrainersListRequest(username, password, trainerUsernames));
    }
}
