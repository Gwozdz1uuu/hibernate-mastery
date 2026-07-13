package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.util.PasswordGenerator;
import com.gwozdz1uu.hibernate_mastery.util.UsernameGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class TraineeService {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final TrainingDAO trainingDAO;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeService(
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

    private Trainee authenticate(String username, String password) {
        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
        if (!trainee.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password for: " + username);
        }
        return trainee;
    }

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setActive(true);

        String username = usernameGenerator.generateUsername(firstName, lastName, traineeDAO.findAll());
        trainee.setUsername(username);
        trainee.setPassword(passwordGenerator.generatePassword());

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        return traineeDAO.create(trainee);
    }

    public boolean matchCredentials(String username, String password) {
        return traineeDAO.findByUsername(username)
                .map(t -> Objects.equals(t.getPassword(), password))
                .orElse(false);
    }

    public Trainee getByUsername(String username, String password) {
        return authenticate(username, password);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = authenticate(username, oldPassword);
        trainee.setPassword(newPassword);
        traineeDAO.update(trainee);
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
        Trainee trainee = authenticate(username, password);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setActive(isActive);
        return traineeDAO.update(trainee);
    }

    public void setActive(String username, String password, boolean isActive) {
        Trainee trainee = authenticate(username, password);
        trainee.setActive(isActive);
        traineeDAO.update(trainee);
    }

    public void deleteByUsername(String username, String password) {
        authenticate(username, password);
        traineeDAO.deleteByUsername(username);
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
        return trainingDAO.findByTraineeCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Trainer> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = authenticate(username, password);

        List<Trainer> trainers = trainerUsernames.stream()
                .map(trainerUsername -> trainerDAO.findBuUsername(trainerUsername)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername)))
                .collect(Collectors.toList());

        trainee.setTrainers(trainers);
        traineeDAO.update(trainee);
        return trainee.getTrainers();
    }
}
