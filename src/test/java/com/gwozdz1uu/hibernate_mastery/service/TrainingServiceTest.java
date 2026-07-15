package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.AbstractIntegrationTest;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.EntityNotFoundException;
import com.gwozdz1uu.hibernate_mastery.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingServiceTest extends AbstractIntegrationTest {

    @Autowired
    private TrainingService trainingService;

    @Test
    void addTraining_shouldPersistTraining() {
        TrainingType type = persistType("Resistance");

        Trainee trainee = createTrainee("Yana", "Fox");
        Trainer trainer = createTrainer("Zed", "Wolf", type);

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
        TrainingType type = persistType("HIIT");

        Trainee trainee = createTrainee("Test", "User");
        Trainer trainer = createTrainer("Coach", "HIIT", type);

        assertThrows(AuthenticationException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), "wrongPassword",
                        trainer.getUsername(), "Session", type.getId(),
                        LocalDate.now(), 30));
    }

    @Test
    void addTraining_blankTrainingName_shouldThrow() {
        TrainingType type = persistType("HIIT2");

        Trainee trainee = createTrainee("Val", "User");
        Trainer trainer = createTrainer("Coach2", "HIIT", type);

        assertThrows(ValidationException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), trainee.getPassword(),
                        trainer.getUsername(), "  ", type.getId(),
                        LocalDate.now(), 30));
    }

    @Test
    void addTraining_invalidTypeId_shouldThrow() {
        TrainingType type = persistType("HIIT4");

        Trainee trainee = createTrainee("Val3", "User");
        Trainer trainer = createTrainer("Coach4", "HIIT", type);

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), trainee.getPassword(),
                        trainer.getUsername(), "Session", 999999L,
                        LocalDate.now(), 30));
    }

    @Test
    void addTraining_zeroDuration_shouldThrow() {
        TrainingType type = persistType("HIIT3");

        Trainee trainee = createTrainee("Val2", "User");
        Trainer trainer = createTrainer("Coach3", "HIIT", type);

        assertThrows(ValidationException.class,
                () -> trainingService.addTraining(
                        trainee.getUsername(), trainee.getPassword(),
                        trainer.getUsername(), "Session", type.getId(),
                        LocalDate.now(), 0));
    }
}
