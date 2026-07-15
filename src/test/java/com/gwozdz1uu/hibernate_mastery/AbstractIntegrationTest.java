package com.gwozdz1uu.hibernate_mastery;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;
import com.gwozdz1uu.hibernate_mastery.service.TraineeService;
import com.gwozdz1uu.hibernate_mastery.service.TrainerService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected EntityManager em;

    @Autowired
    protected TraineeService traineeService;

    @Autowired
    protected TrainerService trainerService;

    protected TrainingType persistType(String name) {
        TrainingType type = new TrainingType(name);
        em.persist(type);
        em.flush();
        return type;
    }

    protected Trainee createTrainee(String firstName, String lastName) {
        return traineeService.createTrainee(firstName, lastName, null, null);
    }

    protected Trainer createTrainer(String firstName, String lastName, TrainingType type) {
        return trainerService.createTrainer(firstName, lastName, type);
    }
}
