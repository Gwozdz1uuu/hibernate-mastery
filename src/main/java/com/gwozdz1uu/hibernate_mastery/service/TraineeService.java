package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TraineeDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.util.PasswordGenerator;
import com.gwozdz1uu.hibernate_mastery.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDAO traineeDAO;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee createTrainee(Trainee trainee) {
        logger.info("Creating trainee profile for: {} {}", trainee.getFirstName(), trainee.getLastName());
        String username = usernameGenerator.generateUsername(
                trainee.getFirstName(), trainee.getLastName(), traineeDAO.findAll());
        trainee.setUsername(username);
        trainee.setPassword(passwordGenerator.generatePassword());
        Trainee created = traineeDAO.create(trainee);
        logger.info("Trainee profile created with username: {}", created.getUsername());
        return created;
    }

    public Trainee updateTrainee(Trainee trainee) {
        logger.info("Updating trainee profile with id: {}", trainee.getId());
        Trainee updated = traineeDAO.update(trainee);
        logger.info("Trainee profile updated successfully for id: {}", updated.getId());
        return updated;
    }

    public boolean deleteTrainee(Long id) {
        logger.info("Deleting trainee profile with id: {}", id);
        boolean result = traineeDAO.delete(id);
        if (result) {
            logger.info("Trainee profile deleted successfully for id: {}", id);
        } else {
            logger.warn("Trainee profile not found for deletion with id: {}", id);
        }
        return result;
    }

    public Optional<Trainee> selectTrainee(Long id) {
        logger.info("Selecting trainee profile with id: {}", id);
        Optional<Trainee> trainee = traineeDAO.findById(id);
        trainee.ifPresentOrElse(
                t -> logger.info("Found trainee: {}", t.getUsername()),
                () -> logger.warn("Trainee not found with id: {}", id)
        );
        return trainee;
    }

    public List<Trainee> selectAllTrainees() {
        logger.info("Selecting all trainee profiles");
        List<Trainee> trainees = traineeDAO.findAll();
        logger.info("Found {} trainees", trainees.size());
        return trainees;
    }
}
