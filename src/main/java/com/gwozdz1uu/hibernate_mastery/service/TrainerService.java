package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.dao.TrainerDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.util.PasswordGenerator;
import com.gwozdz1uu.hibernate_mastery.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDAO trainerDAO;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer createTrainer(Trainer trainer) {
        logger.info("Creating trainer profile for: {} {}", trainer.getFirstName(), trainer.getLastName());
        String username = usernameGenerator.generateUsername(
                trainer.getFirstName(), trainer.getLastName(), trainerDAO.findAll());
        trainer.setUsername(username);
        trainer.setPassword(passwordGenerator.generatePassword());
        Trainer created = trainerDAO.create(trainer);
        logger.info("Trainer profile created with username: {}", created.getUsername());
        return created;
    }

    public Trainer updateTrainer(Trainer trainer) {
        logger.info("Updating trainer profile with id: {}", trainer.getId());
        Trainer updated = trainerDAO.update(trainer);
        logger.info("Trainer profile updated successfully for id: {}", updated.getId());
        return updated;
    }

    public Optional<Trainer> selectTrainer(Long id) {
        logger.info("Selecting trainer profile with id: {}", id);
        Optional<Trainer> trainer = trainerDAO.findById(id);
        trainer.ifPresentOrElse(
                t -> logger.info("Found trainer: {}", t.getUsername()),
                () -> logger.warn("Trainer not found with id: {}", id)
        );
        return trainer;
    }

    public List<Trainer> selectAllTrainers() {
        logger.info("Selecting all trainer profiles");
        List<Trainer> trainers = trainerDAO.findAll();
        logger.info("Found {} trainers", trainers.size());
        return trainers;
    }
}
