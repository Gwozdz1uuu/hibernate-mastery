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

class TraineeServiceTest extends AbstractIntegrationTest {

    @Autowired
    private TrainingService trainingService;

    @Test
    void createTrainee_shouldGenerateUsernameAndPassword() {
        Trainee t = createTrainee("John", "Doe");

        assertNotNull(t.getId());
        assertEquals("John.Doe", t.getUsername());
        assertNotNull(t.getPassword());
        assertEquals(10, t.getPassword().length());
        assertTrue(t.isActive());
    }

    @Test
    void createTrainee_duplicateName_shouldAppendSerial() {
        createTrainee("John", "Doe");
        Trainee second = createTrainee("John", "Doe");

        assertEquals("John.Doe1", second.getUsername());
    }

    @Test
    void createTrainee_blankFirstName_shouldThrow() {
        assertThrows(ValidationException.class,
                () -> traineeService.createTrainee("", "Doe", null, null));
    }

    @Test
    void matchCredentials_correctPassword_shouldReturnTrue() {
        Trainee t = createTrainee("Jane", "Doe");
        assertTrue(traineeService.matchCredentials(t.getUsername(), t.getPassword()));
    }

    @Test
    void matchCredentials_wrongPassword_shouldReturnFalse() {
        Trainee t = createTrainee("Jane", "Doe");
        assertFalse(traineeService.matchCredentials(t.getUsername(), "wrongpass"));
    }

    @Test
    void getByUsername_validCredentials_shouldReturnTrainee() {
        Trainee created = createTrainee("Alice", "Smith");
        Trainee selected = traineeService.getByUsername(created.getUsername(), created.getPassword());
        assertEquals(created.getId(), selected.getId());
    }

    @Test
    void getByUsername_invalidPassword_shouldThrow() {
        Trainee created = createTrainee("Alice", "Smith");
        assertThrows(AuthenticationException.class,
                () -> traineeService.getByUsername(created.getUsername(), "wrong"));
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        Trainee trainee = createTrainee("Bob", "Jones");
        String oldPassword = trainee.getPassword();

        traineeService.changePassword(trainee.getUsername(), oldPassword, "newSecret99");

        assertTrue(traineeService.matchCredentials(trainee.getUsername(), "newSecret99"));
        assertFalse(traineeService.matchCredentials(trainee.getUsername(), oldPassword));
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        Trainee trainee = createTrainee("Carl", "White");
        LocalDate dob = LocalDate.of(1990, 5, 15);

        Trainee updated = traineeService.updateProfile(
                trainee.getUsername(),
                trainee.getPassword(),
                "Charles",
                "Whitman",
                dob,
                "123 Main St",
                true
        );

        assertEquals("Charles", updated.getFirstName());
        assertEquals("Whitman", updated.getLastName());
        assertEquals(dob, updated.getDateOfBirth());
        assertEquals("123 Main St", updated.getAddress());
    }

    @Test
    void updateProfile_blankLastName_shouldThrow() {
        Trainee trainee = createTrainee("Dan", "Lee");
        assertThrows(ValidationException.class,
                () -> traineeService.updateProfile(
                        trainee.getUsername(), trainee.getPassword(),
                        "Dan", "  ", null, null, true));
    }

    @Test
    void setActive_shouldToggleActiveStatus() {
        Trainee trainee = createTrainee("Eva", "Stone");
        assertTrue(trainee.isActive());

        traineeService.setActive(trainee.getUsername(), trainee.getPassword(), false);

        Trainee deactivated = traineeService.getByUsername(trainee.getUsername(), trainee.getPassword());
        assertFalse(deactivated.isActive());
    }

    @Test
    void updateTrainersList_shouldReplaceList() {
        TrainingType type = persistType("Yoga");

        Trainee trainee = createTrainee("Alice", "Smith");
        String pw = trainee.getPassword();

        Trainer trainer1 = createTrainer("Bob", "Brown", type);
        Trainer trainer2 = createTrainer("Carl", "White", type);

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
        TrainingType type = persistType("Cardio");

        Trainee trainee = createTrainee("Dan", "Lee");
        String traineeUsername = trainee.getUsername();
        String traineePassword = trainee.getPassword();

        Trainer trainer = createTrainer("Eva", "Stone", type);

        Training training = trainingService.addTraining(
                traineeUsername,
                traineePassword,
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

    @Test
    void getTrainings_shouldFilterByCriteria() {
        TrainingType yoga = persistType("YogaFilter");
        TrainingType cardio = persistType("CardioFilter");

        Trainee trainee = createTrainee("Filter", "Trainee");
        Trainer yogaTrainer = createTrainer("Yoga", "Master", yoga);
        Trainer cardioTrainer = createTrainer("Cardio", "Coach", cardio);

        trainingService.addTraining(
                trainee.getUsername(), trainee.getPassword(),
                yogaTrainer.getUsername(), "Yoga Session", yoga.getId(),
                LocalDate.of(2025, 6, 1), 60);
        trainingService.addTraining(
                trainee.getUsername(), trainee.getPassword(),
                cardioTrainer.getUsername(), "Cardio Session", cardio.getId(),
                LocalDate.of(2025, 8, 1), 45);

        List<Training> filtered = traineeService.getTrainings(
                trainee.getUsername(), trainee.getPassword(),
                LocalDate.of(2025, 5, 1), LocalDate.of(2025, 7, 1),
                "Yoga", "YogaFilter"
        );

        assertEquals(1, filtered.size());
        assertEquals("Yoga Session", filtered.get(0).getTrainingName());
    }
}
