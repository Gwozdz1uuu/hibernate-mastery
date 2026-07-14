package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.exception.ValidationException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainingServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private EntityManager em;

    @Test
    void addTraining_shouldPersistTraining() {
        TrainingType type = new TrainingType("Resistance");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Yana", "Fox", null, null);
        Trainer trainer = trainerService.createTrainer("Zed", "Wolf", type);

        Training training = trainingService.addTraining(
                trainee.getUsername(),
                trainee.getPassword(),
                trainer.getUsername(),
                "Leg day",
                type.getId(),
                LocalDate.now(),
                60
        );

        assertNotNull(training.getId());
        assertEquals("Leg day", training.getTrainingName());
        assertEquals(type.getId(), training.getTrainingType().getId());

        em.flush();
        em.clear();

        Training reloaded = em.find(Training.class, training.getId());
        assertNotNull(reloaded);
    }

    @Test
    void addTraining_wrongPassword_shouldThrow() {
        TrainingType type = new TrainingType("HIIT");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Test", "User", null, null);
        Trainer trainer = trainerService.createTrainer("Coach", "HIIT", type);

        assertThrows(AuthenticationException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), "wrongPassword",
                        trainer.getUsername(), "Session", type.getId(),
                        LocalDate.now(), 30));
    }

    @Test
    void addTraining_blankTrainingName_shouldThrow() {
        TrainingType type = new TrainingType("HIIT2");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Val", "User", null, null);
        Trainer trainer = trainerService.createTrainer("Coach2", "HIIT", type);

        assertThrows(ValidationException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), trainee.getPassword(),
                        trainer.getUsername(), "  ", type.getId(),
                        LocalDate.now(), 30));
    }

    @Test
    void addTraining_invalidTypeId_shouldThrow() {
        TrainingType type = new TrainingType("HIIT4");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Val3", "User", null, null);
        Trainer trainer = trainerService.createTrainer("Coach4", "HIIT", type);

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), trainee.getPassword(),
                        trainer.getUsername(), "Session", 999999L,
                        LocalDate.now(), 30));
    }

    @Test
    void addTraining_zeroDuration_shouldThrow() {
        TrainingType type = new TrainingType("HIIT3");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Val2", "User", null, null);
        Trainer trainer = trainerService.createTrainer("Coach3", "HIIT", type);

        assertThrows(ValidationException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), trainee.getPassword(),
                        trainer.getUsername(), "Session", type.getId(),
                        LocalDate.now(), 0));
    }
}
