package com.gwozdz1uu.hibernate_mastery.service;


import com.gwozdz1uu.hibernate_mastery.dao.TrainingDAO;
import com.gwozdz1uu.hibernate_mastery.entity.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public Training createTraining(Training training) {
        logger.info("Creating training: {}", training.getTrainingName());
        Training created = trainingDAO.create(training);
        logger.info("Training created with id: {}", created.getId());
        return created;
    }

    public Optional<Training> selectTraining(Long id) {
        logger.info("Selecting training with id: {}", id);
        Optional<Training> training = trainingDAO.findById(id);
        training.ifPresentOrElse(
                t -> logger.info("Found training: {}", t.getTrainingName()),
                () -> logger.warn("Training not found with id: {}", id)
        );
        return training;
    }

    public List<Training> selectAllTrainings() {
        logger.info("Selecting all trainings");
        List<Training> trainings = trainingDAO.findAll();
        logger.info("Found {} trainings", trainings.size());
        return trainings;
    }
}
