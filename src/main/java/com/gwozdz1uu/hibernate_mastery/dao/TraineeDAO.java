package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;
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
public class TraineeDAO {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDAO.class);

    private final Map<Long, Trainee> traineeStorage;
    private final AtomicLong idCounter = new AtomicLong(0);

    @Autowired
    public TraineeDAO(@Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
        long maxId = traineeStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        idCounter.set(maxId);
    }

    public Trainee create(Trainee trainee) {
        long newId = idCounter.incrementAndGet();
        trainee.setId(newId);
        traineeStorage.put(newId, trainee);
        logger.debug("Created trainee with id: {}", newId);
        return trainee;
    }

    public Optional<Trainee> findById(Long id) {
        logger.debug("Finding trainee by id: {}", id);
        return Optional.ofNullable(traineeStorage.get(id));
    }

    public List<Trainee> findAll() {
        logger.debug("Finding all trainees, total count: {}", traineeStorage.size());
        return new ArrayList<>(traineeStorage.values());
    }

    public Trainee update(Trainee trainee) {
        if (trainee.getId() == null || !traineeStorage.containsKey(trainee.getId())) {
            logger.warn("Attempted to update non-existing trainee with id: {}", trainee.getId());
            throw new IllegalArgumentException("Trainee not found with id: " + trainee.getId());
        }
        traineeStorage.put(trainee.getId(), trainee);
        logger.debug("Updated trainee with id: {}", trainee.getId());
        return trainee;
    }

    public boolean delete(Long id) {
        Trainee removed = traineeStorage.remove(id);
        if (removed != null) {
            logger.debug("Deleted trainee with id: {}", id);
            return true;
        }
        logger.warn("Attempted to delete non-existing trainee with id: {}", id);
        return false;
    }
}
