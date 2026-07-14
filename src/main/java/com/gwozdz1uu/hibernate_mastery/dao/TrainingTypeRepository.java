package com.gwozdz1uu.hibernate_mastery.dao;

import com.gwozdz1uu.hibernate_mastery.entity.TrainingType;

import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findById(Long id);
}
