package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Trainer> findUnassignedToTrainee(String traineeUsername);
}
