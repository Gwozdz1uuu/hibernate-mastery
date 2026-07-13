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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private EntityManager em;

    @Test
    void createTrainee_shouldGenerateUsernameAndPassword() {
        Trainee t = traineeService.createTrainee("John", "Doe", null, null);

        assertNotNull(t.getId());
        assertEquals("John.Doe", t.getUsername());
        assertNotNull(t.getPassword());
        assertEquals(10, t.getPassword().length());
        assertTrue(t.isActive());
    }

    @Test
    void matchCredentials_wrongPassword_shouldReturnFalse() {
        Trainee t = traineeService.createTrainee("Jane", "Doe", null, null);
        assertFalse(traineeService.matchCredentials(t.getUsername(), "wrongpass"));
    }

    @Test
    void updateTrainersList_shouldReplaceList() {
        TrainingType type = new TrainingType("Yoga");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Alice", "Smith", null, null);
        String pw = trainee.getPassword();

        Trainer trainer1 = trainerService.createTrainer("Bob", "Brown", type);
        Trainer trainer2 = trainerService.createTrainer("Carl", "White", type);

        List<Trainer> updated = traineeService.updateTrainersList(
                trainee.getUsername(),
                pw,
                List.of(trainer1.getUsername(), trainer2.getUsername())
        );

        assertEquals(2, updated.size());
        assertTrue(updated.stream().anyMatch(t -> t.getUsername().equals(trainer1.getUsername())));
        assertTrue(updated.stream().anyMatch(t -> t.getUsername().equals(trainer2.getUsername())));
    }

    @Test
    void deleteByUsername_shouldCascadeDeleteTrainings() {
        TrainingType type = new TrainingType("Cardio");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Dan", "Lee", null, null);
        String traineeUsername = trainee.getUsername();
        String traineePassword = trainee.getPassword();

        Trainer trainer = trainerService.createTrainer("Eva", "Stone", type);

        Training training = trainingService.addTraining(
                traineeUsername,
                trainer.getUsername(),
                "Morning session",
                type.getId(),
                LocalDate.now(),
                45
        );
        assertNotNull(training.getId());

        traineeService.deleteByUsername(traineeUsername, traineePassword);
        em.flush();
        em.clear();

        Long trainingsCount = em.createQuery(
                        "SELECT COUNT(t) FROM Training t WHERE t.trainee.username = :u",
                        Long.class
                )
                .setParameter("u", traineeUsername)
                .getSingleResult();

        assertEquals(0L, trainingsCount);
    }
}

