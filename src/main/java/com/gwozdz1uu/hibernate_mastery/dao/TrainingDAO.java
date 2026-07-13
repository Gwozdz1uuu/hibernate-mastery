package com.epam.gym_crm_system.dao;

import com.epam.gym_crm_system.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TrainingDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);

    private final Map<Long, Training> trainingStorage;
    private final AtomicLong idCounter = new AtomicLong(0);

    @Autowired
    public TrainingDAO(@Qualifier("trainingStorage") Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
        long maxId = trainingStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        idCounter.set(maxId);
    }

    public Training create(Training training) {
        long newId = idCounter.incrementAndGet();
        training.setId(newId);
        trainingStorage.put(newId, training);
        logger.debug("Created training with id: {}", newId);
        return training;
    }

    public Optional<Training> findById(Long id) {
        logger.debug("Finding training by id: {}", id);
        return Optional.ofNullable(trainingStorage.get(id));
    }

    public List<Training> findAll() {
        logger.debug("Finding all trainings, total count: {}", trainingStorage.size());
        return new ArrayList<>(trainingStorage.values());
    }
}
