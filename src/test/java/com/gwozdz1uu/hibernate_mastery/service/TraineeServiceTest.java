package com.gwozdz1uu.hibernate_mastery.service;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.exception.AuthenticationException;
import com.gwozdz1uu.hibernate_mastery.exception.ValidationException;
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

    private TrainingType persistType(String name) {
        TrainingType type = new TrainingType(name);
        em.persist(type);
        em.flush();
        return type;
    }

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
    void createTrainee_duplicateName_shouldAppendSerial() {
        traineeService.createTrainee("John", "Doe", null, null);
        Trainee second = traineeService.createTrainee("John", "Doe", null, null);

        assertEquals("John.Doe1", second.getUsername());
    }

    @Test
    void createTrainee_blankFirstName_shouldThrow() {
        assertThrows(ValidationException.class,
                () -> traineeService.createTrainee("", "Doe", null, null));
    }

    @Test
    void matchCredentials_correctPassword_shouldReturnTrue() {
        Trainee t = traineeService.createTrainee("Jane", "Doe", null, null);
        assertTrue(traineeService.matchCredentials(t.getUsername(), t.getPassword()));
    }

    @Test
    void matchCredentials_wrongPassword_shouldReturnFalse() {
        Trainee t = traineeService.createTrainee("Jane", "Doe", null, null);
        assertFalse(traineeService.matchCredentials(t.getUsername(), "wrongpass"));
    }

    @Test
    void getByUsername_validCredentials_shouldReturnTrainee() {
        Trainee created = traineeService.createTrainee("Alice", "Smith", null, null);
        Trainee selected = traineeService.getByUsername(created.getUsername(), created.getPassword());
        assertEquals(created.getId(), selected.getId());
    }

    @Test
    void getByUsername_invalidPassword_shouldThrow() {
        Trainee created = traineeService.createTrainee("Alice", "Smith", null, null);
        assertThrows(AuthenticationException.class,
                () -> traineeService.getByUsername(created.getUsername(), "wrong"));
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        Trainee trainee = traineeService.createTrainee("Bob", "Jones", null, null);
        String oldPassword = trainee.getPassword();

        traineeService.changePassword(trainee.getUsername(), oldPassword, "newSecret99");

        assertTrue(traineeService.matchCredentials(trainee.getUsername(), "newSecret99"));
        assertFalse(traineeService.matchCredentials(trainee.getUsername(), oldPassword));
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        Trainee trainee = traineeService.createTrainee("Carl", "White", null, null);
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
        Trainee trainee = traineeService.createTrainee("Dan", "Lee", null, null);
        assertThrows(ValidationException.class,
                () -> traineeService.updateProfile(
                        trainee.getUsername(), trainee.getPassword(),
                        "Dan", "  ", null, null, true));
    }

    @Test
    void setActive_shouldToggleActiveStatus() {
        Trainee trainee = traineeService.createTrainee("Eva", "Stone", null, null);
        assertTrue(trainee.isActive());

        traineeService.setActive(trainee.getUsername(), trainee.getPassword(), false);

        Trainee deactivated = traineeService.getByUsername(trainee.getUsername(), trainee.getPassword());
        assertFalse(deactivated.isActive());
    }

    @Test
    void updateTrainersList_shouldReplaceList() {
        TrainingType type = persistType("Yoga");

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
        TrainingType type = persistType("Cardio");

        Trainee trainee = traineeService.createTrainee("Dan", "Lee", null, null);
        String traineeUsername = trainee.getUsername();
        String traineePassword = trainee.getPassword();

        Trainer trainer = trainerService.createTrainer("Eva", "Stone", type);

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

        Trainee trainee = traineeService.createTrainee("Filter", "Trainee", null, null);
        Trainer yogaTrainer = trainerService.createTrainer("Yoga", "Master", yoga);
        Trainer cardioTrainer = trainerService.createTrainer("Cardio", "Coach", cardio);

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
