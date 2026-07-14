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
class TrainerServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private EntityManager em;

    private TrainingType persistType(String name) {
        TrainingType type = new TrainingType(name);
        em.persist(type);
        em.flush();
        return type;
    }

    @Test
    void createTrainer_shouldGenerateUsernameAndPassword() {
        TrainingType type = persistType("Stretching");

        Trainer t = trainerService.createTrainer("Tom", "Taylor", type);

        assertNotNull(t.getId());
        assertEquals("Tom.Taylor", t.getUsername());
        assertNotNull(t.getPassword());
        assertEquals(10, t.getPassword().length());
        assertTrue(t.isActive());
        assertEquals(type.getTrainingTypeName(), t.getSpecialization().getTrainingTypeName());
    }

    @Test
    void createTrainer_nullSpecialization_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer("Tom", "Taylor", null));
    }

    @Test
    void matchCredentials_correctPassword_shouldReturnTrue() {
        TrainingType type = persistType("Pilates");
        Trainer t = trainerService.createTrainer("Ann", "Bell", type);
        assertTrue(trainerService.matchCredentials(t.getUsername(), t.getPassword()));
    }

    @Test
    void matchCredentials_wrongPassword_shouldReturnFalse() {
        TrainingType type = persistType("Pilates2");
        Trainer t = trainerService.createTrainer("Ann", "Bell", type);
        assertFalse(trainerService.matchCredentials(t.getUsername(), "wrongpass"));
    }

    @Test
    void getByUsername_validCredentials_shouldReturnTrainer() {
        TrainingType type = persistType("Boxing");
        Trainer created = trainerService.createTrainer("Max", "Power", type);
        Trainer selected = trainerService.getByUsername(created.getUsername(), created.getPassword());
        assertEquals(created.getId(), selected.getId());
    }

    @Test
    void getByUsername_invalidPassword_shouldThrow() {
        TrainingType type = persistType("Boxing2");
        Trainer created = trainerService.createTrainer("Max", "Power", type);
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getByUsername(created.getUsername(), "wrong"));
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        TrainingType type = persistType("CrossFit");
        Trainer trainer = trainerService.createTrainer("Pat", "Strong", type);
        String oldPassword = trainer.getPassword();

        trainerService.changePassword(trainer.getUsername(), oldPassword, "newTrainer99");

        assertTrue(trainerService.matchCredentials(trainer.getUsername(), "newTrainer99"));
        assertFalse(trainerService.matchCredentials(trainer.getUsername(), oldPassword));
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        TrainingType type1 = persistType("TypeA");
        TrainingType type2 = persistType("TypeB");
        Trainer trainer = trainerService.createTrainer("Sam", "Fit", type1);

        Trainer updated = trainerService.updateProfile(
                trainer.getUsername(),
                trainer.getPassword(),
                "Samuel",
                "Fitness",
                type2,
                true
        );

        assertEquals("Samuel", updated.getFirstName());
        assertEquals("Fitness", updated.getLastName());
        assertEquals(type2.getId(), updated.getSpecialization().getId());
    }

    @Test
    void setActive_shouldToggleActiveStatus() {
        TrainingType type = persistType("Kickboxing");
        Trainer trainer = trainerService.createTrainer("Kim", "Lee", type);
        assertTrue(trainer.isActive());

        trainerService.setActive(trainer.getUsername(), trainer.getPassword(), false);

        Trainer deactivated = trainerService.getByUsername(trainer.getUsername(), trainer.getPassword());
        assertFalse(deactivated.isActive());
    }

    @Test
    void getUnassignedTrainers_shouldExcludeAlreadyAssigned() {
        TrainingType type = persistType("Zumba");

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

    @Test
    void getTrainings_shouldFilterByCriteria() {
        TrainingType type = persistType("Swimming");

        Trainee trainee1 = traineeService.createTrainee("Amy", "Swimmer", null, null);
        Trainee trainee2 = traineeService.createTrainee("Ben", "Runner", null, null);
        Trainer trainer = trainerService.createTrainer("Coach", "Swim", type);

        trainingService.addTraining(
                trainee1.getUsername(), trainee1.getPassword(),
                trainer.getUsername(), "Amy Swim", type.getId(),
                LocalDate.of(2025, 3, 1), 60);
        trainingService.addTraining(
                trainee2.getUsername(), trainee2.getPassword(),
                trainer.getUsername(), "Ben Swim", type.getId(),
                LocalDate.of(2025, 4, 1), 45);

        List<Training> filtered = trainerService.getTrainings(
                trainer.getUsername(), trainer.getPassword(),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31),
                "Amy"
        );

        assertEquals(1, filtered.size());
        assertEquals("Amy Swim", filtered.get(0).getTrainingName());
    }
}
