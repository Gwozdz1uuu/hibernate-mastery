package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TrainerRepository;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingRepository;
import com.gwozdz1uu.hibernate_mastery.dto.*;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
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

@Service
@Transactional
@RequiredArgsConstructor
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final AuthenticationService authenticationService;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileCreationHelper profileCreationHelper;
    private final PasswordEncoder passwordEncoder;
    private final DtoValidator dtoValidator;

    public Trainer createTrainer(CreateTrainerRequest request) {
        dtoValidator.validate(request);

        Trainer trainer = new Trainer();
        String rawPassword = profileCreationHelper.setupNewProfile(
                trainer, request.firstName(), request.lastName(), trainerRepository::existsByUsername);
        trainer.setSpecialization(request.specialization());

        Trainer created = trainerRepository.save(trainer);
        created.setPassword(rawPassword);
        log.info("Created trainer profile: username={}", created.getUsername());
        return created;
    }

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        return createTrainer(new CreateTrainerRequest(firstName, lastName, specialization));
    }

    public boolean matchCredentials(String username, String password) {
        boolean matched = authenticationService.trainerCredentialsMatch(username, password);
        log.debug("Trainer credential match for {}: {}", username, matched);
        return matched;
    }

    public Trainer getByUsername(String username, String password) {
        authenticationService.authenticateTrainer(username, password);
        Trainer trainer = trainerRepository.findByUsernameWithSpecialization(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        log.info("Selected trainer profile: username={}", username);
        return trainer;
    }

    public void changePassword(ChangePasswordRequest request) {
        dtoValidator.validate(request);
        Trainer trainer = authenticationService.authenticateTrainer(request.username(), request.oldPassword());
        trainer.setPassword(passwordEncoder.encode(request.newPassword()));
        trainerRepository.update(trainer);
        log.info("Changed password for trainer: username={}", request.username());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        changePassword(new ChangePasswordRequest(username, oldPassword, newPassword));
    }

    public Trainer updateProfile(UpdateTrainerRequest request) {
        dtoValidator.validate(request);

        Trainer trainer = authenticationService.authenticateTrainer(request.username(), request.password());
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setSpecialization(request.specialization());
        trainer.setActive(request.active());
        Trainer updated = trainerRepository.update(trainer);
        log.info("Updated trainer profile: username={}", request.username());
        return updated;
    }

    public Trainer updateProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            TrainingType specialization,
            boolean isActive
    ) {
        return updateProfile(new UpdateTrainerRequest(
                username, password, firstName, lastName, specialization, isActive));
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.setActive(isActive);
        trainerRepository.update(trainer);
        log.info("Set trainer active={} for username={}", isActive, username);
    }

    public List<Training> getTrainings(TrainerTrainingSearchRequest request) {
        authenticationService.authenticateTrainer(request.username(), request.password());
        List<Training> trainings = trainingRepository.findByTrainerCriteria(
                request.username(), request.fromDate(), request.toDate(), request.traineeName());
        log.info("Retrieved {} trainings for trainer: username={}", trainings.size(), request.username());
        return trainings;
    }

    public List<Training> getTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        return getTrainings(new TrainerTrainingSearchRequest(
                username, password, fromDate, toDate, traineeName));
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername, String traineePassword) {
        authenticationService.authenticateTrainee(traineeUsername, traineePassword);
        List<Trainer> trainers = trainerRepository.findUnassignedToTrainee(traineeUsername);
        log.info("Retrieved {} unassigned trainers for trainee: username={}", trainers.size(), traineeUsername);
        return trainers;
    }
}
