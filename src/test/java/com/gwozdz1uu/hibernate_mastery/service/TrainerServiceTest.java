package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private EntityManager em;

    @Test
    void createTrainer_shouldGenerateUsernameAndPassword() {
        TrainingType type = new TrainingType("Stretching");
        em.persist(type);
        em.flush();

        Trainer t = trainerService.createTrainer("Tom", "Taylor", type);

        assertNotNull(t.getId());
        assertEquals("Tom.Taylor", t.getUsername());
        assertNotNull(t.getPassword());
        assertEquals(10, t.getPassword().length());
        assertTrue(t.isActive());
        assertEquals(type.getTrainingTypeName(), t.getSpecialization().getTrainingTypeName());
    }

    @Test
    void getUnassignedTrainers_shouldExcludeAlreadyAssigned() {
        TrainingType type = new TrainingType("Zumba");
        em.persist(type);
        em.flush();

        Trainee trainee = traineeService.createTrainee("Uma", "Green", null, null);
        String traineeUsername = trainee.getUsername();
        String traineePassword = trainee.getPassword();

        Trainer assigned = trainerService.createTrainer("Vic", "Black", type);
        Trainer unassigned = trainerService.createTrainer("Wes", "Gray", type);

        traineeService.updateTrainersList(traineeUsername, traineePassword, List.of(assigned.getUsername()));

        List<Trainer> result = trainerService.getUnassignedTrainers(traineeUsername, traineePassword);

        assertTrue(result.stream().noneMatch(t -> t.getUsername().equals(assigned.getUsername())));
        assertTrue(result.stream().anyMatch(t -> t.getUsername().equals(unassigned.getUsername())));
    }
}

