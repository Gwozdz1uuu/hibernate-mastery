package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.AbstractIntegrationTest;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerServiceTest extends AbstractIntegrationTest {

    @Autowired
    private TrainingService trainingService;

    @Test
    void createTrainer_shouldGenerateUsernameAndPassword() {
        TrainingType type = persistType("Stretching");

        Trainer t = createTrainer("Tom", "Taylor", type);

        assertNotNull(t.getId());
        assertEquals("Tom.Taylor", t.getUsername());
        assertNotNull(t.getPassword());
        assertEquals(10, t.getPassword().length());
        assertTrue(t.isActive());
        assertEquals(type.getTrainingTypeName(), t.getSpecialization().getTrainingTypeName());
    }

    @Test
    void createTrainer_nullSpecialization_shouldThrow() {
        assertThrows(ValidationException.class,
                () -> trainerService.createTrainer("Tom", "Taylor", null));
    }

    @Test
    void matchCredentials_correctPassword_shouldReturnTrue() {
        TrainingType type = persistType("Pilates");
        Trainer t = createTrainer("Ann", "Bell", type);
        assertTrue(trainerService.matchCredentials(t.getUsername(), t.getPassword()));
    }

    @Test
    void matchCredentials_wrongPassword_shouldReturnFalse() {
        TrainingType type = persistType("Pilates2");
        Trainer t = createTrainer("Ann", "Bell", type);
        assertFalse(trainerService.matchCredentials(t.getUsername(), "wrongpass"));
    }

    @Test
    void getByUsername_validCredentials_shouldReturnTrainer() {
        TrainingType type = persistType("Boxing");
        Trainer created = createTrainer("Max", "Power", type);
        Trainer selected = trainerService.getByUsername(created.getUsername(), created.getPassword());
        assertEquals(created.getId(), selected.getId());
    }

    @Test
    void getByUsername_invalidPassword_shouldThrow() {
        TrainingType type = persistType("Boxing2");
        Trainer created = createTrainer("Max", "Power", type);
        assertThrows(AuthenticationException.class,
                () -> trainerService.getByUsername(created.getUsername(), "wrong"));
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        TrainingType type = persistType("CrossFit");
        Trainer trainer = createTrainer("Pat", "Strong", type);
        String oldPassword = trainer.getPassword();

        trainerService.changePassword(trainer.getUsername(), oldPassword, "newTrainer99");

        assertTrue(trainerService.matchCredentials(trainer.getUsername(), "newTrainer99"));
        assertFalse(trainerService.matchCredentials(trainer.getUsername(), oldPassword));
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        TrainingType type1 = persistType("TypeA");
        TrainingType type2 = persistType("TypeB");
        Trainer trainer = createTrainer("Sam", "Fit", type1);

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
        Trainer trainer = createTrainer("Kim", "Lee", type);
        assertTrue(trainer.isActive());

        trainerService.setActive(trainer.getUsername(), trainer.getPassword(), false);

        Trainer deactivated = trainerService.getByUsername(trainer.getUsername(), trainer.getPassword());
        assertFalse(deactivated.isActive());
    }

    @Test
    void getUnassignedTrainers_shouldExcludeAlreadyAssigned() {
        TrainingType type = persistType("Zumba");

        Trainee trainee = createTrainee("Uma", "Green");
        String traineeUsername = trainee.getUsername();
        String traineePassword = trainee.getPassword();

        Trainer assigned = createTrainer("Vic", "Black", type);
        Trainer unassigned = createTrainer("Wes", "Gray", type);

        traineeService.updateTrainersList(traineeUsername, traineePassword, List.of(assigned.getUsername()));

        List<Trainer> result = trainerService.getUnassignedTrainers(traineeUsername, traineePassword);

        assertTrue(result.stream().noneMatch(t -> t.getUsername().equals(assigned.getUsername())));
        assertTrue(result.stream().anyMatch(t -> t.getUsername().equals(unassigned.getUsername())));
    }

    @Test
    void getTrainings_shouldFilterByCriteria() {
        TrainingType type = persistType("Swimming");

        Trainee trainee1 = createTrainee("Amy", "Swimmer");
        Trainee trainee2 = createTrainee("Ben", "Runner");
        Trainer trainer = createTrainer("Coach", "Swim", type);

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
