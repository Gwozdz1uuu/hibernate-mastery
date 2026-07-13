package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
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
}

