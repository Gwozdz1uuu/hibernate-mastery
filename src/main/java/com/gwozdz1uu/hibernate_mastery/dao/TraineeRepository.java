package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.Trainee;

import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    Optional<Trainee> findByUsername(String username);
    void deleteByUsername(String username);
    boolean existsByUsername(String username);
}
