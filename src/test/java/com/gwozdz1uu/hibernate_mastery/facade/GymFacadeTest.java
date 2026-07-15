package com.gwozdz1uu.hibernate_mastery.facade;

import com.gwozdz1uu.hibernate_mastery.AbstractIntegrationTest;
import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GymFacadeTest extends AbstractIntegrationTest {

    @Autowired
    private GymFacade gymFacade;

    @Test
    void createAndSelectTraineeProfile() {
        Trainee created = gymFacade.createTraineeProfile("Facade", "Trainee", null, null);
        Trainee selected = gymFacade.selectTraineeByUsername(created.getUsername(), created.getPassword());
        assertEquals(created.getId(), selected.getId());
    }

    @Test
    void createAndSelectTrainerProfile() {
        TrainingType type = persistType("FacadeType");
        Trainer created = gymFacade.createTrainerProfile("Facade", "Trainer", type);
        Trainer selected = gymFacade.selectTrainerByUsername(created.getUsername(), created.getPassword());
        assertEquals(created.getId(), selected.getId());
    }

    @Test
    void authenticateTraineeAndTrainer() {
        Trainee trainee = gymFacade.createTraineeProfile("Auth", "Trainee", null, null);
        TrainingType type = persistType("AuthType");
        Trainer trainer = gymFacade.createTrainerProfile("Auth", "Trainer", type);

        assertTrue(gymFacade.authenticateTrainee(trainee.getUsername(), trainee.getPassword()));
        assertTrue(gymFacade.authenticateTrainer(trainer.getUsername(), trainer.getPassword()));
        assertFalse(gymFacade.authenticateTrainee(trainee.getUsername(), "wrong"));
    }

    @Test
    void addTrainingAndGetTraineeTrainings() {
        TrainingType type = persistType("FacadeTraining");
        Trainee trainee = gymFacade.createTraineeProfile("Train", "One", null, null);
        Trainer trainer = gymFacade.createTrainerProfile("Train", "Coach", type);

        Training added = gymFacade.addTraining(
                trainee.getUsername(), trainee.getPassword(),
                trainer.getUsername(), "Facade Session", type.getId(),
                LocalDate.now(), 90
        );
        assertNotNull(added.getId());

        List<Training> trainings = gymFacade.getTraineeTrainings(
                trainee.getUsername(), trainee.getPassword(),
                null, null, null, null
        );
        assertEquals(1, trainings.size());
    }

    @Test
    void activateDeactivateAndUpdateProfiles() {
        TrainingType type = persistType("ActiveType");
        Trainee trainee = gymFacade.createTraineeProfile("Active", "User", null, null);
        Trainer trainer = gymFacade.createTrainerProfile("Active", "Coach", type);

        gymFacade.activateOrDeactivateTrainee(trainee.getUsername(), trainee.getPassword(), false);
        Trainee deactivated = gymFacade.selectTraineeByUsername(trainee.getUsername(), trainee.getPassword());
        assertFalse(deactivated.isActive());

        gymFacade.activateOrDeactivateTrainer(trainer.getUsername(), trainer.getPassword(), false);
        Trainer deactivatedTrainer = gymFacade.selectTrainerByUsername(trainer.getUsername(), trainer.getPassword());
        assertFalse(deactivatedTrainer.isActive());
    }
}
