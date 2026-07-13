package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Trainer;
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
public class TrainerDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

    private final Map<Long, Trainer> trainerStorage;
    private final AtomicLong idCounter = new AtomicLong(0);

    @Autowired
    public TrainerDAO(@Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
        long maxId = trainerStorage.keySet().stream().mapToLong(Long::longValue).max().orElse(0);
        idCounter.set(maxId);
    }

    public Trainer create(Trainer trainer) {
        long newId = idCounter.incrementAndGet();
        trainer.setId(newId);
        trainerStorage.put(newId, trainer);
        logger.debug("Created trainer with id: {}", newId);
        return trainer;
    }

    public Optional<Trainer> findById(Long id) {
        logger.debug("Finding trainer by id: {}", id);
        return Optional.ofNullable(trainerStorage.get(id));
    }

    public List<Trainer> findAll() {
        logger.debug("Finding all trainers, total count: {}", trainerStorage.size());
        return new ArrayList<>(trainerStorage.values());
    }

    public Trainer update(Trainer trainer) {
        if (trainer.getId() == null || !trainerStorage.containsKey(trainer.getId())) {
            logger.warn("Attempted to update non-existing trainer with id: {}", trainer.getId());
            throw new IllegalArgumentException("Trainer not found with id: " + trainer.getId());
        }
        trainerStorage.put(trainer.getId(), trainer);
        logger.debug("Updated trainer with id: {}", trainer.getId());
        return trainer;
    }
}
